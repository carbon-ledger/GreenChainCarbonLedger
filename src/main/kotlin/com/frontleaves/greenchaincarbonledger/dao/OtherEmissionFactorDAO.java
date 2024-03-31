package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.OtherEmissionFactorMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.OtherEmissionFactorDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 用于存放对OtherEmissionFactor表的数据库操作
 * @author FALSHALCK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OtherEmissionFactorDAO {
    private final OtherEmissionFactorMapper otherEmissionFactorMapper;

    /**
     * 通过名字获取碳排放影子
     * @param name-名字
     * @return 碳排放因子对象
     */
    public OtherEmissionFactorDO getFactorByName(String name){
        log.info("[DAO] 执行 getFactorByName");
        log.info("/t Mysql 查询");
        return otherEmissionFactorMapper.getFactorByName(name);
    }
}
