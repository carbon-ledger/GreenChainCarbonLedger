package com.frontleaves.greenchaincarbonledger.config

import org.hyperledger.fabric.gateway.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Fabric 配置文件
 */
@Configuration
open class HyperLedgerFabricConfiguration(
    private val hyperLedgerFabricProperties: HyperLedgerFabricProperties
) {

    @Bean
    @Throws(Exception::class)
    open fun gateway(): Gateway {
        val certificateReader =
            Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.certificatePath), StandardCharsets.UTF_8)
        val certificate = Identities.readX509Certificate(certificateReader)
        val privateKeyReader =
            Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.privateKeyPath), StandardCharsets.UTF_8)
        val privateKey = Identities.readPrivateKey(privateKeyReader)

        val wallet = Wallets.newInMemoryWallet()
        wallet.put("user1", Identities.newX509Identity("Org1MSP", certificate, privateKey))

        val builder = Gateway.createBuilder()
            .identity(wallet, "user1")
            .networkConfig(Paths.get(hyperLedgerFabricProperties.networkConnectionConfigPath))
        return builder.connect()
    }

    @Bean
    open fun network(gateway: Gateway): Network {
        return gateway.getNetwork(hyperLedgerFabricProperties.channel)
    }

    @Bean
    open fun catContract(network: Network): Contract {
        return network.getContract("hyperledger-fabric-contract-java-demo", "CarbonTradingContract")
    }
}