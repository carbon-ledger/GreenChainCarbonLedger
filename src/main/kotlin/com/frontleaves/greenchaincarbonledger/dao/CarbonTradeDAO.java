package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonTypeMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTypeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 对于碳交易的数据查询和更新
 * @author FLASHLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonTradeDAO {
    private final CarbonMapper carbonMapper;
}
