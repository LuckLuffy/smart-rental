<!--
  RentalGuide.vue（租客端）- 租房指引 / AI 智能推荐页面（系统最复杂页面）

  作用：
    1. 提供偏好问卷（预算/面积/户型/位置）和自由文本描述两种输入方式
    2. 调用后端 DeepSeek 大模型 API（houseApi.recommend）进行智能匹配分析
    3. 以匹配度列表展示推荐结果，每条结果包含匹配分数（高/中/低）、
       匹配理由标签，支持查看房源详情和直接租房操作

  交互流程：
    填写偏好表单或文本描述 -> 点击"AI智能匹配" -> 调用后端推荐接口
    -> 后端调用 DeepSeek 大模型分析用户需求与房源的匹配度
    -> 返回按匹配度排序的房源列表 -> 用户可查看详情或直接下单

  推荐结果展示：
    - 高匹配（>= 60 分）：红色标签
    - 中匹配（>= 35 分）：黄色标签
    - 低匹配（< 35 分）：灰色标签
    - 每条结果附带匹配理由标签（如"预算符合"、"面积合适"等）

  关键接口调用：
    - houseApi.recommend  -> AI 智能推荐（DeepSeek）
    - orderApi.save       -> 创建租房订单
-->

<template>
  <div>
    <!-- ========== 偏好与需求输入区 ========== -->
    <el-card style="margin-bottom:20px">
      <template #header><h4>🧭 租房指引 - DeepSeek AI 智能推荐</h4></template>

      <!-- 结构化偏好问卷：四列布局，每个维度一个下拉选择或输入框 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="预算范围"><el-select v-model="prefs.budget" placeholder="不限" clearable style="width:100%">
            <el-option label="1000元以下" value="0-1000" /><el-option label="1000-2000元" value="1000-2000" />
            <el-option label="2000-4000元" value="2000-4000" /><el-option label="4000-6000元" value="4000-6000" />
            <el-option label="6000元以上" value="6000-99999" />
          </el-select></el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="面积需求"><el-select v-model="prefs.area" placeholder="不限" clearable style="width:100%">
            <el-option label="30㎡以下" value="0-30" /><el-option label="30-60㎡" value="30-60" />
            <el-option label="60-100㎡" value="60-100" /><el-option label="100㎡以上" value="100-999" />
          </el-select></el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="户型偏好"><el-select v-model="prefs.roomType" placeholder="不限" clearable style="width:100%">
            <el-option label="一室一卫" value="一室一卫" /><el-option label="两室一厅" value="两室一厅" />
            <el-option label="三室两厅" value="三室两厅" /><el-option label="其他" value="其他" />
          </el-select></el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="位置偏好"><el-input v-model="prefs.location" placeholder="如：朝阳、海淀" clearable /></el-form-item>
        </el-col>
      </el-row>

      <!-- 自然语言描述区：AI 会结合结构化偏好一起分析 -->
      <el-form-item label="描述你的需求" style="margin-top:10px">
        <el-input v-model="description" type="textarea" :rows="3"
          placeholder="用你自己的话描述理想房源，AI会自动分析匹配。&#10;例如：我想要一个离地铁近的两室一厅，阳光好，安静，精装修，预算3000左右" />
      </el-form-item>

      <!-- 触发匹配的按钮 -->
      <el-button type="primary" size="large" @click="startMatch" :loading="matching" style="margin-top:10px">
        🔍 AI智能匹配
      </el-button>
      <!-- 匹配结果数量提示 -->
      <span v-if="resultCount > 0" style="margin-left:16px;color:#67C23A">
        共找到 {{ resultCount }} 套推荐房源，按匹配度排序
      </span>
    </el-card>

    <!-- ========== 推荐结果列表 ========== -->
    <el-card v-if="results.length > 0">
      <template #header><h4>推荐房源（匹配度从高到低）</h4></template>
      <!-- 遍历每条推荐结果 -->
      <div v-for="item in results" :key="item.house.id" style="border-bottom:1px solid #ebeef5;padding:16px 0">
        <el-row :gutter="20" align="middle">
          <el-col :span="14">
            <div style="display:flex;align-items:center;gap:12px">
              <!-- 匹配度标签：分数越高颜色越深 -->
              <el-tag :type="item.score >= 60 ? 'danger' : item.score >= 35 ? 'warning' : 'info'" effect="dark" size="large">
                {{ item.score >= 60 ? '高匹配' : item.score >= 35 ? '中匹配' : '低匹配' }} {{ item.score }}分
              </el-tag>
              <div>
                <h4 style="margin:0 0 4px 0">{{ item.house.title }}</h4>
                <span style="color:#909399;font-size:13px">{{ item.house.address }} | {{ item.house.area }}㎡ | {{ item.house.type }}</span>
              </div>
            </div>
          </el-col>
          <el-col :span="4">
            <span style="color:#f56c6c;font-size:20px;font-weight:bold">¥{{ item.house.price }}/月</span>
          </el-col>
          <el-col :span="6" style="text-align:right">
            <el-button type="primary" @click="viewDetail(item)">查看详情</el-button>
            <el-button type="success" @click="rentHouse(item.house)" :disabled="item.house.status !== 'AVAILABLE'">立即租房</el-button>
          </el-col>
        </el-row>
        <!-- 匹配理由标签：显示每条匹配理由（如"预算符合、面积合适"等） -->
        <div style="margin-top:8px;margin-left:70px;display:flex;gap:12px;flex-wrap:wrap">
          <el-tag v-for="reason in item.reasons" :key="reason" size="small" type="success" effect="plain">{{ reason }}</el-tag>
        </div>
      </div>
    </el-card>

    <!-- ========== 房源详情弹窗 ========== -->
    <el-dialog v-model="detailVisible" title="房源详情" width="550px">
      <template v-if="detailHouse">
        <h3>{{ detailHouse.title }}</h3>
        <el-descriptions :column="2" border style="margin-top:16px">
          <el-descriptions-item label="地址">{{ detailHouse.address }}</el-descriptions-item>
          <el-descriptions-item label="月租"><span style="color:#f56c6c;font-weight:bold">¥{{ detailHouse.price }}</span></el-descriptions-item>
          <el-descriptions-item label="面积">{{ detailHouse.area }}㎡</el-descriptions-item>
          <el-descriptions-item label="户型">{{ detailHouse.type }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag :type="detailHouse.status==='AVAILABLE'?'success':'info'">{{ detailHouse.status==='AVAILABLE'?'待租':'已租' }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="房东ID">{{ detailHouse.landlordId }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ detailHouse.description || '暂无描述' }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer><el-button @click="detailVisible=false">关闭</el-button><el-button type="success" @click="rentHouse(detailHouse);detailVisible=false" :disabled="detailHouse?.status!=='AVAILABLE'">立即租房</el-button></template>
    </el-dialog>

    <!-- ========== 确认租房弹窗 ========== -->
    <el-dialog v-model="rentVisible" title="确认租房" width="400px">
      <p>确定要租 <b>{{ rentForm.title }}</b> 吗？</p>
      <el-form :model="rentForm" label-width="80px" style="margin-top:16px">
        <el-form-item label="开始日期"><el-date-picker v-model="rentForm.startDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="rentForm.endDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="rentVisible=false">取消</el-button><el-button type="primary" @click="submitRent">确认下单</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * RentalGuide.vue（租客端）- AI智能推荐与租房指引逻辑
 *
 * 核心逻辑：
 *   1. 收集用户的结构化偏好（预算/面积/户型/位置）和自然语言描述
 *   2. 将以上信息发送到后端，后端调用 DeepSeek 大模型分析用户需求与
 *      房源的语义匹配度，返回按匹配度降序排列的推荐列表
 *   3. 每条推荐包含匹配分数和理由标签，用户可查看详情或直接下单
 *
 * 容错设计：
 *   如果 AI 服务不可用（网络异常或后端异常），给出友好提示。
 */
import { ref, reactive } from 'vue'
import { houseApi, orderApi } from '../../api'
import { ElMessage } from 'element-plus'

const user = JSON.parse(localStorage.getItem('user') || 'null')  // 当前登录租客

// ========== 输入数据 ==========
const description = ref('')                          // 自然语言需求描述
const prefs = reactive({ budget: '', area: '', roomType: '', location: '' })  // 结构化偏好

// ========== 匹配状态 & 结果 ==========
const matching = ref(false)            // 是否正在匹配中（控制按钮加载状态）
const results = ref([])                // 后端返回的推荐结果数组
const resultCount = ref(0)             // 推荐结果数量
const aiMode = ref(true)               // 标记当前使用的是 AI 模式（备用字段，可用于降级策略）

// ========== 房源详情弹窗 ==========
const detailVisible = ref(false)
const detailHouse = ref(null)

// ========== 租房弹窗 ==========
const rentVisible = ref(false)
const rentForm = reactive({ id: null, title: '', houseId: null, landlordId: null, startDate: '', endDate: '' })

/**
 * 【核心方法】开始 AI 智能匹配
 *
 * 逻辑：
 *   1. 校验至少填写了一项偏好或有文本描述
 *   2. 调用 houseApi.recommend 向后端发送偏好数据 + 描述文本
 *   3. 后端调用 DeepSeek 大模型进行分析，返回匹配度排名的房源列表
 *   4. 每条结果包含：house（房源信息）/ score（匹配分数 0-100）/ reasons（匹配理由数组）
 *   5. 根据结果数量给出不同的反馈提示
 */
const startMatch = async () => {
  // 至少需要一项输入条件
  if (!description.value && !prefs.budget && !prefs.area && !prefs.roomType && !prefs.location) {
    ElMessage.warning('请至少填写一项偏好或描述你的需求')
    return
  }
  matching.value = true
  results.value = []      // 清空上次结果
  try {
    // 调用后端推荐接口，后端会使用 DeepSeek 大模型进行语义分析
    const res = await houseApi.recommend({
      description: description.value,
      budget: prefs.budget,
      area: prefs.area,
      roomType: prefs.roomType,
      location: prefs.location
    })
    if (res.code === 200) {
      results.value = res.data || []
      resultCount.value = results.value.length
      aiMode.value = true
      if (results.value.length === 0) {
        ElMessage.info('AI没有找到匹配房源，请尝试调整偏好条件')
      } else {
        ElMessage.success(`DeepSeek AI 为您找到 ${resultCount.value} 套房源`)
      }
    } else {
      ElMessage.error(res.message || '推荐失败')
    }
  } catch (e) {
    // 网络异常或 AI 服务不可用的兜底提示
    ElMessage.warning('AI服务暂时不可用，请稍后重试')
  } finally {
    matching.value = false
  }
}

/**
 * 查看房源详情
 * 将选中的结果中的 house 数据填充到详情弹窗中并显示
 */
const viewDetail = (item) => {
  detailHouse.value = item.house
  detailVisible.value = true
}

/**
 * 打开租房确认弹窗
 * 将选中的房源信息填入租赁表单
 */
const rentHouse = (house) => {
  rentForm.id = null; rentForm.title = house.title; rentForm.houseId = house.id; rentForm.landlordId = house.landlordId
  rentForm.startDate = ''; rentForm.endDate = ''
  rentVisible.value = true
}

/**
 * 提交租赁订单
 * 校验日期后调用 orderApi.save 创建 PENDING 状态的订单
 */
const submitRent = async () => {
  if (!rentForm.startDate || !rentForm.endDate) { ElMessage.warning('请选择起止日期'); return }
  try {
    const res = await orderApi.save({
      houseId: rentForm.houseId,
      tenantId: user.id,
      landlordId: rentForm.landlordId,
      startDate: rentForm.startDate,
      endDate: rentForm.endDate,
      totalAmount: 0,                // 总金额由后端计算或后续更新
      status: 'PENDING'              // 初始状态，待房东确认
    })
    if (res.code === 200) { ElMessage.success('下单成功，等待房东确认'); rentVisible.value = false }
    else { ElMessage.error(res.message || '下单失败') }
  } catch { ElMessage.error('下单失败') }
}
</script>
