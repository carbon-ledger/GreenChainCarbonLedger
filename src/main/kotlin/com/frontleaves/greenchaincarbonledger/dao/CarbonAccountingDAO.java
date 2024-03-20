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
}
