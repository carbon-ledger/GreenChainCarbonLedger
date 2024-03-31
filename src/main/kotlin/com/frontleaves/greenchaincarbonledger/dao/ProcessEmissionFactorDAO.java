package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.DesulfurizationFactorMapper;
import com.frontleaves.greenchaincarbonledger.mappers.ProcessEmissionFactorMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.DesulfurizationFactorDO;
import com.frontleaves.greenchaincarbonledger.models.doData.ProcessEmissionFactorDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 用于存放对于排放因子表的操作
 * @author FALSHLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ProcessEmissionFactorDAO {
    private final ProcessEmissionFactorMapper processEmissionFactorMapper;
    private final DesulfurizationFactorMapper desulfurizationFactorMapper;

    /**
     * 通过名字来获取对应的碳排放因子
     * @param name-名字
     * @return ProcessEmissionFactor 对象
     */
    public ProcessEmissionFactorDO getFactorByName(String name){
        log.info("[DAO] 执行 getFactorByName");
        log.info("/t Mysql 查询");
        return processEmissionFactorMapper.getFactorByName(name);
    }

    public DesulfurizationFactorDO getDesFactorByName(String name){
        log.info("[DAO] 执行 getDesFactorByName");
        log.info("/t Mysql 查询");
        return desulfurizationFactorMapper.getDesFactorByName(name);
    }

}
