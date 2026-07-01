package com.smartrental.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密工具类
 * <p>
 * 提供用户密码的 MD5 加密存储与验证功能。
 * 使用固定的 salt（加盐）值对明文密码进行加盐哈希，
 * 增强密码存储的安全性，防止彩虹表攻击。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public class MD5Util {

    /** 加密盐值，用于加盐哈希，增加密码破解难度 */
    private static final String SALT = "smartrental2026";

    /**
     * 对明文密码进行 MD5 加盐加密
     * <p>
     * 将明文密码与固定 SALT 拼接后，计算 MD5 摘要，
     * 结果以 32 位小写十六进制字符串形式返回。
     * </p>
     *
     * @param plainText 明文密码
     * @return 加盐后的 MD5 哈希值（32 位小写十六进制字符串）
     * @throws RuntimeException 当系统不支持 MD5 算法时抛出
     */
    public static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将密码与盐值拼接后进行摘要计算
            byte[] digest = md.digest((plainText + SALT).getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 将每个字节转为两位十六进制数
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * 验证明文密码是否与已存储的 MD5 哈希值匹配
     * <p>
     * 对输入的明文密码执行相同的加盐 MD5 加密后，
     * 与存储的哈希值进行等值比较。
     * </p>
     *
     * @param plainText 待验证的明文密码
     * @param md5Hash   已存储的 MD5 哈希值
     * @return 匹配返回 true，否则返回 false
     */
    public static boolean matches(String plainText, String md5Hash) {
        return md5(plainText).equals(md5Hash);
    }
}
