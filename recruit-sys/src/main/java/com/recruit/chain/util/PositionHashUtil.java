package com.recruit.chain.util;

import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruit.model.PositionDO;
import lombok.extern.slf4j.Slf4j;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 职位数据哈希工具类
 * 将岗位核心信息序列化后计算 SHA-256 哈希值
 * 用于上链存储和一致性校验
 *
 * @author SuoMingFei
 * @since 2024-09-01
 */
@Slf4j
public class PositionHashUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 计算职位核心信息的 SHA-256 哈希值
     * 使用 TreeMap 保证字段顺序一致，确保相同数据产生相同哈希
     *
     * @param position 职位实体
     * @return SHA-256 哈希的十六进制字符串
     */
    public static String computePositionHash(PositionDO position) {
        try {
            // 使用有序Map保证序列化顺序一致
            SortedMap<String, Object> dataMap = new TreeMap<>();
            dataMap.put("id", position.getId());
            dataMap.put("title", position.getTitle() != null ? position.getTitle().trim() : "");
            dataMap.put("requirement", position.getRequirement() != null ? position.getRequirement().trim() : "");
            dataMap.put("quantity", position.getQuantity());
            dataMap.put("city", position.getCity() != null ? position.getCity().trim() : "");
            dataMap.put("salaryUp", position.getSalaryUp());
            dataMap.put("salaryDown", position.getSalaryDown());
            dataMap.put("categoryId", position.getCategoryId());
            dataMap.put("hrId", position.getHrId());
            dataMap.put("companyId", position.getCompanyId());

            // 序列化为JSON
            String json = objectMapper.writeValueAsString(dataMap);

            // 计算 SHA-256
            String hash = DigestUtil.sha256Hex(json);
            log.debug("职位[{}]哈希计算完成: {}", position.getId(), hash);
            return hash;

        } catch (Exception e) {
            log.error("职位哈希计算失败", e);
            throw new RuntimeException("哈希计算失败", e);
        }
    }

    /**
     * 生成幂等校验Key
     * 基于 positionId + dataHash 组合生成，用于防止重复上链
     *
     * @param positionId 职位ID
     * @param dataHash   数据哈希
     * @return 幂等Key
     */
    public static String generateIdempotentKey(Integer positionId, String dataHash) {
        String raw = "position:" + positionId + ":" + dataHash;
        return DigestUtil.sha256Hex(raw);
    }

    /**
     * 将职位核心数据转为JSON字符串（用于链上存储摘要）
     *
     * @param position 职位实体
     * @return JSON字符串
     */
    public static String toJson(PositionDO position) {
        try {
            SortedMap<String, Object> dataMap = new TreeMap<>();
            dataMap.put("positionId", position.getId());
            dataMap.put("title", position.getTitle());
            dataMap.put("city", position.getCity());
            dataMap.put("salaryRange", position.getSalaryDown() + "-" + position.getSalaryUp());
            dataMap.put("quantity", position.getQuantity());
            dataMap.put("timestamp", System.currentTimeMillis());
            return objectMapper.writeValueAsString(dataMap);
        } catch (Exception e) {
            log.error("职位数据JSON序列化失败", e);
            return "{}";
        }
    }
}
