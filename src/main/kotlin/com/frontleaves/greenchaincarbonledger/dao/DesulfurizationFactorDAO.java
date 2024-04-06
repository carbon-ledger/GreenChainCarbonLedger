package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.DesulfurizationFactorMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.DesulfurizationFactorDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 脱硫排放因子DAO
 * <hr/>
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class DesulfurizationFactorDAO {
    private final DesulfurizationFactorMapper desulfurizationFactorMapper;

    /**
     * 根据名称获取脱硫排放因子
     *
     * @param name 排放因子名称（英文）
     * @return DesulfurizationFactorDO
     */
    public DesulfurizationFactorDO getDesFactorByName(String name){
        log.info("[DAO] 执行 getDesFactorByName");
        log.info("\t> Mysql 查询");
        return desulfurizationFactorMapper.getDesFactorByName(name);
    }

    /**
     * 获取所有的脱硫排放因子
     *
     * @return List<DesulfurizationFactorDO>
     */
    public List<DesulfurizationFactorDO> getDesulfurizationFactorList() {
        log.info("[DAO] 执行 getDesulfurizationFactorList");
        log.info("\t> Mysql 查询");
        return desulfurizationFactorMapper.getDesulfurizationFactorList();
    }
}
