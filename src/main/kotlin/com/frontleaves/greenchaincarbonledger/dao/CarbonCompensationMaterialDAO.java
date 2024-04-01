package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonCompensationMaterialMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonCompensationMaterialDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 存放用于对CarbonCompensationMaterial表的操作
 * @author FALSHLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonCompensationMaterialDAO {
    private final CarbonCompensationMaterialMapper carbonCompensationMaterialMapper;

    public Boolean insertCarbonCompensationMaterial(CarbonCompensationMaterialDO carbonCompensationMaterialDO){
        log.info("[DAO] 执行 insertCarbonCompensationMaterial");
        log.info("\t> Mysql 插入");
        return carbonCompensationMaterialMapper.insertCarbonCompensationMaterial(carbonCompensationMaterialDO);
    }
}
