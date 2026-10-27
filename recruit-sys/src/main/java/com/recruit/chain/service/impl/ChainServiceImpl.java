package com.recruit.chain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recruit.chain.config.ChainProperties;
import com.recruit.chain.mapper.ChainRecordMapper;
import com.recruit.chain.model.ChainRecordDO;
import com.recruit.chain.service.ChainService;
import com.recruit.chain.util.PositionHashUtil;
import com.recruit.model.PositionDO;
import com.recruit.mapper.PositionMapper;
import lombok.extern.slf4j.Slf4j;
import org.chainmaker.pb.common.ResultOuterClass.TxResponse;
import org.chainmaker.pb.common.ResultOuterClass.TxStatusCode;
import org.chainmaker.sdk.ChainClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 长安链服务实现 — 核心链交互逻辑
 *
 * @author SuoMingFei
 * @since 2024-09-01
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "chain.chang-an.enabled", havingValue = "true", matchIfMissing = true)
public class ChainServiceImpl implements ChainService {

    @Resource
    private ChainClient chainClient;

    @Resource
    private ChainProperties chainProperties;

    @Resource
    private ChainRecordMapper chainRecordMapper;

    @Resource
    private PositionMapper positionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChainRecordDO uploadToChain(Integer positionId, String dataHash, String bizDataJson) throws Exception {
        // 1. 幂等校验
        String idempotentKey = PositionHashUtil.generateIdempotentKey(positionId, dataHash);
        if (isAlreadyOnChain(idempotentKey)) {
            log.warn("职位[{}]数据已上链，幂等拦截", positionId);
            QueryWrapper<ChainRecordDO> wrapper = new QueryWrapper<>();
            wrapper.eq("idempotent_key", idempotentKey);
            wrapper.eq("chain_status", 1);
            return chainRecordMapper.selectOne(wrapper);
        }

        // 2. 构建链上KV键值对
        Map<String, byte[]> paramPairs = new HashMap<>();
        paramPairs.put("position_id", String.valueOf(positionId).getBytes(StandardCharsets.UTF_8));
        paramPairs.put("data_hash", dataHash.getBytes(StandardCharsets.UTF_8));
        paramPairs.put("biz_data", bizDataJson.getBytes(StandardCharsets.UTF_8));
        paramPairs.put("timestamp", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
        paramPairs.put("operator", "recruit_system".getBytes(StandardCharsets.UTF_8));

        // 3. 写入待上链记录
        ChainRecordDO record = new ChainRecordDO();
        record.setBizId(positionId);
        record.setBizType("position");
        record.setDataHash(dataHash);
        record.setChainStatus(0);
        record.setIdempotentKey(idempotentKey);
        record.setChainData(bizDataJson);
        record.setCreateTime(new Date());
        chainRecordMapper.insert(record);

        try {
            // 4. 调用长安链智能合约
            String contractName = chainProperties.getContractName();
            String method = "save_recruit_data";

            TxResponse response = chainClient.invokeContract(
                    contractName, method, null, paramPairs,
                    chainProperties.getTimeout(), 0);

            String txId = response.getTxId();
            if (response.getCode() != TxStatusCode.SUCCESS) {
                throw new Exception("链上交易失败: " + response.getMessage());
            }

            // 5. 更新交易记录
            record.setTxId(txId);
            record.setChainStatus(1);
            record.setChainTime(new Date());
            chainRecordMapper.updateById(record);

            log.info("职位[{}]上链成功, TxId={}", positionId, txId);
            return record;

        } catch (Exception e) {
            record.setChainStatus(2);
            chainRecordMapper.updateById(record);
            log.error("职位[{}]上链失败", positionId, e);
            throw new Exception("区块链上链失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> queryFromChain(String txId) {
        Map<String, String> result = new HashMap<>();
        try {
            org.chainmaker.pb.common.ChainmakerTransaction.TransactionInfo txInfo =
                    chainClient.getTxByTxId(txId, chainProperties.getTimeout());
            if (txInfo != null && txInfo.hasTransaction()) {
                byte[] payloadBytes = txInfo.getTransaction().getPayload().toByteArray();
                String payload = new String(payloadBytes, StandardCharsets.UTF_8);
                result.put("data", payload);
                result.put("txId", txId);
                result.put("status", "confirmed");
            }
        } catch (Exception e) {
            log.error("链上查询失败, TxId={}", txId, e);
            result.put("status", "error");
        }
        return result;
    }

    @Override
    public boolean verifyConsistency(Integer positionId) {
        QueryWrapper<ChainRecordDO> wrapper = new QueryWrapper<>();
        wrapper.eq("biz_id", positionId).eq("biz_type", "position").eq("chain_status", 1)
                .orderByDesc("create_time").last("LIMIT 1");
        ChainRecordDO record = chainRecordMapper.selectOne(wrapper);
        if (record == null || StrUtil.isBlank(record.getTxId())) {
            return false;
        }

        PositionDO position = positionMapper.selectById(positionId);
        if (position == null) return false;

        String localHash = PositionHashUtil.computePositionHash(position);
        boolean consistent = localHash.equals(record.getDataHash());
        if (!consistent) {
            record.setChainStatus(3);
            chainRecordMapper.updateById(record);
        }
        return consistent;
    }

    @Override
    public boolean isAlreadyOnChain(String idempotentKey) {
        QueryWrapper<ChainRecordDO> wrapper = new QueryWrapper<>();
        wrapper.eq("idempotent_key", idempotentKey).eq("chain_status", 1);
        return chainRecordMapper.selectCount(wrapper) > 0;
    }
}
