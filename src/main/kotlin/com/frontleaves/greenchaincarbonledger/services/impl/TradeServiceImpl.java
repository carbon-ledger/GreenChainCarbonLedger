package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.CarbonDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTradeDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.services.TradeService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author FLASHLACK
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {
    private final UserDAO userDAO;
    private final CarbonDAO carbonDAO;
    @NotNull
    @Override
    public ResponseEntity<BaseResponse> deleteTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull String id) {
        //确认用户
        UserDO getAuthUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getAuthUserDO != null) {
            //进行碳交易发布校验
            List<CarbonTradeDO> getCarbonTradeList;
            getCarbonTradeList =carbonDAO.getTradeByUuid(getAuthUserDO.getUuid());
            if (getCarbonTradeList == null){
                return ResultUtil.error(timestamp,ErrorCode.CAN_T_PUBLISH_TRADE);
            }else {
                boolean state = false;
                //校验id是否存在
                for (CarbonTradeDO carbonTradeDO : getCarbonTradeList){
                    if (carbonTradeDO.getId().equals(id)){
                        state =true;
                        break;
                    }
                }
                if(state){
                    //校验要删除的ID的status是否为completed
                    CarbonTradeDO getCarbonTrade =carbonDAO.getTradeById(id);
                    if ("completed".equals(getCarbonTrade.getStatus())){
                        return ResultUtil.error(timestamp,"无法删除已完成交易的碳核算交易",ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                    }else {
                        if ("cancelled".equals(getCarbonTrade.getStatus())){
                            return ResultUtil.error(timestamp,"请勿重复删除",ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                        }else {
                            String status ="cancelled";
                            Boolean result= carbonDAO.deleteTrade(id, status);
                            if (result){
                                return ResultUtil.success(timestamp,"删除成功");
                            }else {
                                return ResultUtil.error(timestamp,"删除失败",ErrorCode.SERVER_INTERNAL_ERROR);
                            }
                        }

                    }

                }else {
                    return ResultUtil.error(timestamp,"请检查要删除的碳交易发布是否存在",ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                }
            }

        }else {
            return ResultUtil.error(timestamp, "未查询到您的组织账号",ErrorCode.UUID_NOT_EXIST);
        }
    }
}
