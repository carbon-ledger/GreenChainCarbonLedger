package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

/**
 * Carbon的数据处理
 *
 * @author FLAHSLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonDAO {
    private final CarbonMapper carbonMapper;

    /**
     * 通过uuid,year来获取fy_carbon_quota表中的所有内容
     * <hr/>
     * 通过uuid,year来获取fy_carbon_quota表中的所有内容
     *
     * @author FLASHLACK
     * @since 2024-03-13
     */
    public CarbonQuotaDO[] getQuotaByUuidYear(@NotNull String uuid, Integer year) {
        log.info("[DAO]执行 getQuotaByUuid操作 ");
        log.info("Mysql 读取");
        return carbonMapper.getQuotaByUuidYear(uuid, year);
    }
}
