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
    public List<CarbonAccountingDO> getCarbonAccountingListByUuid(String uuid){
        log.info("[DAO] 执行 getCarbonAccountingListByUuid");
        log.info("/t >Mysql 读取");
        return carbonAccountingMapper.getCarbonAccountingListByUuid(uuid);
    }

    /**
     * 初始化碳核算数据表
     * @param uuid-组织uuid
     * @param reportId-报告ID
     * @param type-碳排放类型
     * @param period-核算周期
     * @param status-数据校验状态（初始化默认pending）
     * @return 是否初始化成功
     */
    public Boolean initializationCarbonAccounting(String uuid,String reportId,String type,String period,String status){
        log.info("[DAO] 执行 initializationCarbonAccounting");
        log.info("/t Mysql 插入");
        return carbonAccountingMapper.initializationCarbonAccounting(uuid, reportId, type, period, status);
    }

    public Boolean insertCarbonAccounting(CarbonAccountingDO carbonAccountingDO){
        log.info("[DAO] 执行 insertCarbonAccounting");
        log.info("/t Mysql 插入");
        return carbonAccountingMapper.insertCarbonAccounting(carbonAccountingDO);
    }

    /**
     * 通过uuid查询碳核算数据链表 倒序（ID大在上面）
     * @param uuid-组织uuid
     * @return 碳核算数据链表
     */
    public List<CarbonAccountingDO>getCarbonAccountingListByUuidDesc(String uuid){
        log.info("[DAO] 执行 getCarbonAccountingListByUuidDesc");
        log.info("/t Mysql 查询");
        return carbonAccountingMapper.getCarbonAccountingListByUuidDesc(uuid);
    }

    /**
     * 更新碳核算
     * @param emissionsVolume-分排放量
     * @param emissionAmount-总排放量
     * @return 是否完成
     */
    public Boolean updateEmissionByUuidId(String emissionsVolume,Double emissionAmount,String id){
        log.info("[DAO] 执行 updateEmissionByUuidId");
        log.info("/t Mysql 更新");
        return  carbonAccountingMapper.updateEmissionByUuidId(emissionsVolume, emissionAmount,id);
    }

}
