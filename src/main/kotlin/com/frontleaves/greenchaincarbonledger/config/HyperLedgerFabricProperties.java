package com.frontleaves.greenchaincarbonledger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * HyperLedgerFabricProperties
 * <hr/>
 * 用于存放HyperLedgerFabric的配置信息,包括网络连接配置路径、证书路径、私钥路径
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "fabric")
public class HyperLedgerFabricProperties {
    public String mspId;
    public String networkConnectionConfigPath;
    public String certificatePath;
    public String privateKeyPath;
    public String tlsCertPath;
    public String channel;
}
