package com.recruit.chain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 链上一致性校验结果
 * @author SuoMingFei
 * @since 2024-09-01
 */
@Data
@Builder
public class ChainVerifyResult {

    /** 职位ID */
    private Integer positionId;

    /** 校验是否通过 */
    private boolean consistent;

    /** 本地数据哈希 */
    private String localHash;

    /** 链上数据哈希 */
    private String chainHash;

    /** 链上交易ID */
    private String txId;

    /** 区块高度 */
    private Long blockHeight;

    /** 描述信息 */
    private String message;
}
