package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


/**
 * CarbonDAO
 * <hr/>
 * 用于碳交易技术,数据库操作,CarbonDAO
 *
 * @author FLASHLACK AND xiao_lfeng AND DC_DC
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonDAO {
    private final CarbonMapper carbonMapper;

    /**
     * 根据组织uuid和年份获取碳排放配额
     * <hr/>
     * 根据组织uuid和年份获取碳排放配额,返回CarbonQuotaDO
     *
     * @param uuid  组织uuid
     * @param start 开始年份
     * @param end   结束年份
     * @return 返回CarbonQuotaDO
     */
    public ArrayList<CarbonQuotaDO> getQuotaListByOrganizeUuid(String uuid, String start, String end) {
        log.info("[DAO]执行 getQuotaListByOrganizeUuid 操作 ");
        log.info("Mysql 读取");
        return carbonMapper.getQuotaListByOrganizeUuid(uuid, start, end);
    }
}
