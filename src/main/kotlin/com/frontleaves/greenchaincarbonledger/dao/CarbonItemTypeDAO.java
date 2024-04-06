package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonItemTypeMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonItemTypeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用于对CarbonItemType表的数据库操作
 * @author FLASHLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonItemTypeDAO {
    private final CarbonItemTypeMapper carbonItemTypeMapper;

    /**
     * 通过材料名称来获取碳排放类型
     * @param name-材料名称
     * @return carbonItemTypeDO
     */
    public CarbonItemTypeDO getCarbonItemTypeByName(String name){
        log.info("[DAO] 执行 getCarbonItemTypeByName");
        log.info("\t> Mysql 查询");
        return carbonItemTypeMapper.getCarbonItemTypeByName(name);
    }

    /**
     * 获取所有的碳排放类型
     *
     * @return carbonItemTypeDO
     */
    public List<CarbonItemTypeDO> getCarbonItemTypeList() {
        log.info("[DAO] 执行 getCarbonItemTypeList");
        log.info("\t> Mysql 查询");
        return carbonItemTypeMapper.getCarbonItemTypeList();
    }
}
