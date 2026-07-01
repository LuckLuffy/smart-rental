package com.smartrental.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartrental.dto.RecommendResult;
import com.smartrental.entity.House;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * DeepSeek 大模型智能推荐服务类
 * <p>
 * 集成 DeepSeek Chat 大语言模型 API，根据用户输入的偏好信息
 * （预算、面积、户型、位置、文字描述等）对房源列表进行智能匹配和评分，
 * 返回按匹配度排序的推荐结果。当 API 调用失败时，自动降级为
 * 基于简单规则（位置匹配、户型匹配）的本地推荐方案。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@Service
public class DeepSeekService {

    /** DeepSeek API 密钥，从配置文件注入 */
    @Value("${deepseek.api-key}")
    private String apiKey;

    /** DeepSeek API 请求地址，从配置文件注入 */
    @Value("${deepseek.api-url}")
    private String apiUrl;

    /** Jackson JSON 对象映射器，用于序列化和反序列化 */
    private final ObjectMapper mapper = new ObjectMapper();

    /** HTTP 客户端，配置 30 秒连接超时 */
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * 根据用户偏好推荐房源
     * <p>
     * 核心推荐方法。将用户偏好和房源列表构造成提示词（Prompt），
     * 调用 DeepSeek Chat API 获取 AI 推荐的匹配结果。
     * 如果 API 调用失败或返回异常，自动切换到降级方案。
     * </p>
     *
     * @param req    用户推荐请求，包含预算、面积、户型、位置、描述等偏好
     * @param houses 待推荐的房源列表
     * @return 按匹配度评分降序排列的推荐结果列表；若无可用房源则返回空列表
     */
    public List<RecommendResult> recommend(com.smartrental.dto.RecommendRequest req, List<House> houses) {
        // 房源列表为空，直接返回空结果
        if (houses.isEmpty()) return List.of();

        // 构建房源列表文本，格式化输出每个房源的序号、标题、类型、面积、价格、地址和描述
        StringBuilder houseList = new StringBuilder();
        for (int i = 0; i < houses.size(); i++) {
            House h = houses.get(i);
            houseList.append(String.format("[%d] %s | %s | %.0f㎡ | ¥%.0f/月 | %s | %s\n",
                    i + 1, h.getTitle(), h.getType(),
                    h.getArea(), h.getPrice(),
                    h.getAddress(),
                    h.getDescription() != null ? h.getDescription() : "无描述"));
        }

        // 构建用户偏好文本，仅添加用户已填写的偏好项
        StringBuilder userPrefs = new StringBuilder();
        if (req.getBudget() != null && !req.getBudget().isEmpty())
            userPrefs.append("- 预算范围: ").append(req.getBudget()).append("元/月\n");
        if (req.getArea() != null && !req.getArea().isEmpty())
            userPrefs.append("- 面积需求: ").append(req.getArea()).append("㎡\n");
        if (req.getRoomType() != null && !req.getRoomType().isEmpty())
            userPrefs.append("- 户型偏好: ").append(req.getRoomType()).append("\n");
        if (req.getLocation() != null && !req.getLocation().isEmpty())
            userPrefs.append("- 位置偏好: ").append(req.getLocation()).append("\n");

        // 系统提示词：规定 AI 助手的角色和响应格式
        String systemPrompt = """
                你是一个专业的租房推荐助手。根据用户的偏好和文字描述，从房源列表中推荐最合适的房源。
                请严格按以下JSON格式返回，不要输出其他内容：
                {"results":[{"houseIndex":1,"score":85,"reasons":["预算匹配","交通便利"]}]}
                houseIndex是房源列表中的序号(从1开始)，score是0-100的匹配度评分，reasons是2-4个推荐理由(用中文)。
                只返回得分>=30的房源，按score降序排列。""";

        // 用户提示词：包含用户偏好和可租房源列表
        String userPrompt = "用户偏好：\n" + (userPrefs.length() > 0 ? userPrefs.toString() : "无特定偏好") +
                "\n用户描述：" + (req.getDescription() != null ? req.getDescription() : "无") +
                "\n\n可租房源列表：\n" + houseList.toString();

        try {
            // ===== 构建 API 请求体 =====
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", "deepseek-chat");    // 使用的模型名称
            body.put("temperature", 0.3);           // 较低的温度值，使输出更确定
            body.put("max_tokens", 2000);            // 最大输出 token 数
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));

            String json = mapper.writeValueAsString(body);

            // ===== 发送 HTTP POST 请求 =====
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // API 返回非 200 状态码时降级处理
            if (response.statusCode() != 200) {
                System.err.println("DeepSeek API error: " + response.body());
                return fallbackRecommend(req, houses);
            }

            // ===== 解析 API 响应 =====
            JsonNode root = mapper.readTree(response.body());
            // 从 choices[0].message.content 路径提取 AI 返回的文本
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // 提取 JSON 部分（AI 可能使用 markdown 代码块包裹 JSON）
            String jsonContent = content.trim();
            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            }

            // 解析 JSON 结果并组装推荐结果列表
            JsonNode resultNode = mapper.readTree(jsonContent);
            List<RecommendResult> results = new ArrayList<>();
            for (JsonNode r : resultNode.path("results")) {
                int idx = r.path("houseIndex").asInt() - 1;  // 转为 0-based 索引
                // 验证索引有效
                if (idx >= 0 && idx < houses.size()) {
                    int score = r.path("score").asInt();
                    List<String> reasons = new ArrayList<>();
                    for (JsonNode reason : r.path("reasons")) {
                        reasons.add(reason.asText());
                    }
                    results.add(new RecommendResult(houses.get(idx), score, reasons));
                }
            }

            // 按匹配度评分从高到低排序
            results.sort((a, b) -> b.getScore() - a.getScore());
            return results;

        } catch (Exception e) {
            // API 调用异常时降级处理
            System.err.println("DeepSeek API call failed: " + e.getMessage());
            return fallbackRecommend(req, houses);
        }
    }

    /**
     * 降级推荐方案
     * <p>
     * 当 DeepSeek API 调用失败时，采用基于简单规则的本地匹配算法：
     * - 位置匹配：房源地址包含用户输入的位置关键词，加 30 分
     * - 户型匹配：房源类型包含用户输入的户型关键词，加 30 分
     * - 其他默认加分：用户有描述或标题匹配时加少量分
     * 得分超过 10 分的房源才会纳入推荐，最高不超过 70 分。
     * </p>
     *
     * @param req    用户推荐请求
     * @param houses 待推荐的房源列表
     * @return 按匹配度评分降序排列的推荐结果列表
     */
    private List<RecommendResult> fallbackRecommend(com.smartrental.dto.RecommendRequest req, List<House> houses) {
        List<RecommendResult> results = new ArrayList<>();
        for (House h : houses) {
            int score = 10;  // 基础分
            List<String> reasons = new ArrayList<>();

            // 用户填写了预算要求，添加待确认标记
            if (req.getBudget() != null && !req.getBudget().isEmpty()) {
                reasons.add("预算范围待确认");
            }
            // 位置匹配：地址包含用户指定的位置关键词
            if (req.getLocation() != null && !req.getLocation().isEmpty()
                    && h.getAddress() != null && h.getAddress().contains(req.getLocation())) {
                score += 30;
                reasons.add("位置匹配");
            }
            // 户型匹配：房屋类型匹配用户偏好
            if (req.getRoomType() != null && !req.getRoomType().isEmpty()
                    && h.getType() != null && h.getType().contains(req.getRoomType())) {
                score += 30;
                reasons.add("户型匹配");
            }
            // 用户有描述需求，可作参考
            if (req.getDescription() != null && h.getTitle() != null) {
                score += 5;
                reasons.add("可作参考");
            }

            // 仅保留有匹配点的房源（总分 > 10）
            if (score > 10) {
                results.add(new RecommendResult(h, Math.min(score, 70), reasons));
            }
        }
        // 按评分降序排列
        results.sort((a, b) -> b.getScore() - a.getScore());
        return results;
    }
}
