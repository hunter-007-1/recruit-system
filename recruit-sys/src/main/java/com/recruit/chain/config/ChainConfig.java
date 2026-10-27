package com.recruit.chain.config;

import com.recruit.chain.service.ChainService;
import com.recruit.chain.service.impl.ChainServiceImpl;
import org.chainmaker.sdk.ChainClient;
import org.chainmaker.sdk.ChainManager;
import org.chainmaker.sdk.config.ChainClientConfig;
import org.chainmaker.sdk.config.NodeConfig;
import org.chainmaker.sdk.config.SdkConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 长安链 Spring 自动配置
 * 当 chain.chang-an.enabled=true 时初始化链客户端
 * @author SuoMingFei
 * @since 2024-09-01
 */
@Configuration
public class ChainConfig {

    @Resource
    private ChainProperties chainProperties;

    /**
     * 构建长安链 SDK 客户端
     * 使用 ChainManager 创建 ChainClient，通过 gRPC 连接链节点
     */
    @Bean
    @ConditionalOnProperty(name = "chain.chang-an.enabled", havingValue = "true", matchIfMissing = true)
    public ChainClient chainClient() throws Exception {
        // 1. 节点配置
        NodeConfig nodeConfig = new NodeConfig();
        nodeConfig.setNodeAddr(chainProperties.getNodeAddr());
        nodeConfig.setEnableTls(true);
        nodeConfig.setTrustRootPaths(new String[]{chainProperties.getTlsCertPath()});

        // 2. 链客户端配置
        ChainClientConfig chainClientConfig = new ChainClientConfig();
        chainClientConfig.setChainId(chainProperties.getChainId());
        chainClientConfig.setOrgId(chainProperties.getOrgId());
        chainClientConfig.setUserKeyFilePath(chainProperties.getUserKeyPath());
        chainClientConfig.setUserCrtFilePath(chainProperties.getUserCertPath());
        chainClientConfig.setNodes(new NodeConfig[]{nodeConfig});

        // 3. SDK 全局配置
        SdkConfig sdkConfig = new SdkConfig();
        sdkConfig.setChainClient(chainClientConfig);

        // 4. 通过 ChainManager 创建链客户端
        ChainManager chainManager = ChainManager.getInstance();
        return chainManager.createChainClient(sdkConfig);
    }
}
