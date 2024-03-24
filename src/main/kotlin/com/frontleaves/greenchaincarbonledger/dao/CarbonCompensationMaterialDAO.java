package com.frontleaves.greenchaincarbonledger.dao;

/**
 * 存放用于对CarbonCompensationMaterial表的操作
 * @author FALSHLACK
 */

import com.frontleaves.greenchaincarbonledger.mappers.CarbonCompensationMaterialMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonCompensationMaterialDAO {
    private final CarbonCompensationMaterialMapper carbonCompensationMaterialMapper;

    /**
     * 初始化碳核算原料表
     * @param accountId-碳核算数据ID
     * @param rawMaterial-原料
     * @return 是否初始化成功
     */
    public Boolean initializationCarbonCompensationMaterial(String accountId,String rawMaterial){
        log.info("[DAO] 执行 initializationCarbonCompensationMaterial");
        log.info("/t Mysql 插入");
        return carbonCompensationMaterialMapper.initializationCarbonCompensationMaterial(accountId,rawMaterial);
    }
}
