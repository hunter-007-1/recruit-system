package com.recruit.chain.service;

import com.recruit.chain.model.ChainRecordDO;

import java.util.Map;

/**
 * 长安链服务接口 — 对接区块链底层操作
 * 负责：数据上链、链上查询、数据一致性校验
 * @author SuoMingFei
 * @since 2024-09-01
 */
public interface ChainService {

    /**
     * 将职位核心信息哈希后上链存储
     * 使用长安链 SDK 调用智能合约，将数据哈希写入区块链
     *
     * @param positionId  职位ID
     * @param dataHash    职位数据的SHA-256哈希值
     * @param bizDataJson 业务数据JSON（链上存储原始摘要）
     * @return 交易记录（包含TxId、区块高度等）
     * @throws Exception 上链失败时抛出
     */
    ChainRecordDO uploadToChain(Integer positionId, String dataHash, String bizDataJson) throws Exception;

    /**
     * 根据 TxId 从链上查询交易详情
     *
     * @param txId 交易ID
     * @return 链上存储的数据Map
     */
    Map<String, String> queryFromChain(String txId);

    /**
     * 校验本地数据与链上数据的一致性
     * 1. 查询链上交易记录获取存储的哈希值
     * 2. 对本地数据重新计算哈希
     * 3. 比较两个哈希值是否一致
     *
     * @param positionId 职位ID
     * @return true=一致（数据未被篡改）, false=不一致
     */
    boolean verifyConsistency(Integer positionId);

    /**
     * 幂等校验 — 检查该数据是否已经上过链
     * 基于数据哈希生成幂等Key，避免同一份数据重复上链
     *
     * @param idempotentKey 幂等Key
     * @return true=已上链（幂等拦截）, false=未上链（可继续）
     */
    boolean isAlreadyOnChain(String idempotentKey);
}
