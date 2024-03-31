package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonTypeMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTypeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 对于CarbonType表的操作
 * @author FALSHLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonTypeDAO {
    private final CarbonTypeMapper carbonTypeMapper;

    /**
     * 通过碳核算种类名字获取碳核算种类对象
     * @param name-种类名字
     * @return carbonTypeDO
     */
    public CarbonTypeDO getTypeByName(String name){
        log.info("[DAO] 执行 getTypeByName");
        log.info("Mysql 查询");
        return carbonTypeMapper.getTypeByName(name);
    }
}
