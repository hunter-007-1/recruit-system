package com.recruit.chain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 长安链连接配置属性
 * @author SuoMingFei
 * @since 2024-09-01
 */
@Data
@Component
@ConfigurationProperties(prefix = "chain.chang-an")
public class ChainProperties {

    /** 长安链节点 RPC 地址 */
    private String nodeAddr = "127.0.0.1:12301";

    /** 组织ID */
    private String orgId = "organization1";

    /** 链ID */
    private String chainId = "chain1";

    /** 用户证书路径 */
    private String userCertPath;

    /** 用户私钥路径 */
    private String userKeyPath;

    /** TLS 证书路径 */
    private String tlsCertPath;

    /** 合约名称（智能合约） */
    private String contractName = "recruit_contract";

    /** 是否启用链功能 */
    private boolean enabled = true;

    /** 上链超时时间(ms) */
    private int timeout = 10000;

    /** 异步上链重试次数 */
    private int retryTimes = 3;
}
