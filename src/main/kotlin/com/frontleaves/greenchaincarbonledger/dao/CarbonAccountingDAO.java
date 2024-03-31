package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonAccountingMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author DC_DC
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonAccountingDAO {
    private final CarbonAccountingMapper carbonAccountingMapper;
    /**
     * 获取组织碳核算信息
     *
     * @param organizeUuid 企业uuid
     * @param limit  限制
     * @param page   页数
     * @param order  顺序
     * @return 组织碳核算信息列表
     * */
    public List<CarbonAccountingDO> getCarbonAccountingList(String organizeUuid, Integer limit, Integer page, String order){
        log.info("[DAO] 执行 getCarbonAccountingList 方法");
        log.info("\t> Mysql 读取");
        return carbonAccountingMapper.getOrganizeCarbonAccounting(organizeUuid, limit, page, order);
    }

    public Boolean insertCarbonAccounting(CarbonAccountingDO carbonAccountingDO){
        log.info("[DAO] 执行 insertCarbonAccounting");
        log.info("\t> Mysql 插入");
        return carbonAccountingMapper.insertCarbonAccounting(carbonAccountingDO);
    }

    /**
     * 通过 UUID 获取碳核算
     * <hr/>
     * 仅获取最后一次的碳核算的信息，仅返回一个实体类
     *
     * @param uuid 组织账户 UUID
     * @return 谈核算实体类
     */
    public CarbonAccountingDO getLastCarbonAccountingByUuid(String uuid) {
        log.info("[DAO] 执行 getLastCarbonAccountingByUuid");
        log.info("\t> Mysql 查询");
        return carbonAccountingMapper.getLastCarbonAccountingByUuid(uuid);
    }

    /**
     * 更新碳核算
     * @param emissionsVolume-分排放量
     * @param emissionAmount-总排放量
     * @return 是否完成
     */
    public Boolean updateEmissionByUuidId(String emissionsVolume,Double emissionAmount,String id){
        log.info("[DAO] 执行 updateEmissionByUuidId");
        log.info("\t> Mysql 更新");
        return  carbonAccountingMapper.updateEmissionByUuidId(emissionsVolume, emissionAmount,id);
    }

}
