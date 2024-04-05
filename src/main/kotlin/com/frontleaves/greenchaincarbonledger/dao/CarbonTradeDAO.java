package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonTradeMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTradeDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.EditTradeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 对于碳交易的数据查询和更新
 *
 * @author FLASHLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonTradeDAO {
    private final CarbonMapper carbonMapper;
    private final CarbonTradeMapper carbonTradeMapper;

    public void editTrade(String uuid, EditTradeVO editTradeVO, String status, String id) {
        log.info("[DAO] 执行 editTrade");
        log.info("\t> Mysql 更新");
        carbonMapper.updateTradeByUuid(uuid, editTradeVO.getAmount(), editTradeVO.getUnit(), editTradeVO.getText(), status, id);
    }

    /**
     * 获取当前组织的全部碳交易发布信息列表
     *
     * @param uuid-组织uuid
     * @return 当前组织的全部碳交易发布信息列表
     */
    public List<CarbonTradeDO> getTradeListByUuid(String uuid) {
        log.info("[DAO] 执行 getBuyTradeListByUuid");
        return carbonTradeMapper.getTradeListByUuid(uuid);
    }
}
