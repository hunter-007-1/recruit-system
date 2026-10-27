package com.recruit.chain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 链上交易记录表 - 记录每次上链操作的详细信息
 * 用于链上数据与本地数据的一致性校验
 * @author SuoMingFei
 * @since 2024-09-01
 */
@Data
@TableName("chain_record")
public class ChainRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的业务ID（职位ID）
     */
    private Integer bizId;

    /**
     * 业务类型：position / resume / application
     */
    private String bizType;

    /**
     * 业务数据的 SHA-256 哈希值
     */
    private String dataHash;

    /**
     * 链上交易ID（TxId）
     */
    private String txId;

    /**
     * 区块高度
     */
    private Long blockHeight;

    /**
     * 上链状态：0-待上链 1-已上链 2-上链失败 3-校验不一致
     */
    private Integer chainStatus;

    /**
     * 上链时间
     */
    private Date chainTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 幂等校验Key（基于业务数据哈希生成，防重复上链）
     */
    private String idempotentKey;

    /**
     * 链上原始数据（JSON格式，用于一致性比对）
     */
    private String chainData;
}
