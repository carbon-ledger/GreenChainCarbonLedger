package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.CarbonDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTradeDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonTradeListVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackUserVO;
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

import java.util.ArrayList;
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
            getCarbonTradeList = carbonDAO.getTradeByUuid(getAuthUserDO.getUuid());
            if (getCarbonTradeList == null) {
                return ResultUtil.error(timestamp, ErrorCode.CAN_T_PUBLISH_TRADE);
            } else {
                boolean state = false;
                //校验id是否存在
                for (CarbonTradeDO carbonTradeDO : getCarbonTradeList) {
                    if (carbonTradeDO.getId().equals(id)) {
                        state = true;
                        break;
                    }
                }
                if (state) {
                    //校验要删除的ID的status是否为completed
                    CarbonTradeDO getCarbonTrade = carbonDAO.getTradeById(id);
                    if ("completed".equals(getCarbonTrade.getStatus())) {
                        return ResultUtil.error(timestamp, "无法删除已完成交易的碳核算交易", ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                    } else {
                        if ("cancelled".equals(getCarbonTrade.getStatus())) {
                            return ResultUtil.error(timestamp, "请勿重复删除", ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                        } else {
                            String status = "cancelled";
                            Boolean result = carbonDAO.deleteTrade(id, status);
                            if (result) {
                                return ResultUtil.success(timestamp, "删除成功");
                            } else {
                                return ResultUtil.error(timestamp, "删除失败", ErrorCode.SERVER_INTERNAL_ERROR);
                            }
                        }

                    }

                } else {
                    return ResultUtil.error(timestamp, "请检查要删除的碳交易发布是否存在", ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                }
            }

        } else {
            return ResultUtil.error(timestamp, "未查询到您的组织账号", ErrorCode.UUID_NOT_EXIST);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getOwnTradeList(long timestamp, @NotNull HttpServletRequest request, @NotNull String type, String search, String limit, String page, String order) {
        log.info("[Service] 执行 getOwnTradeList");
        UserDO getUser = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUser != null) {
            String getUuid = getUser.getUuid();
            //检查是否发布了碳交易
            if (carbonDAO.getTradeListByUuid(getUuid)) {
                //检查参数
                // 检查参数，如果未设置（即为null），则使用默认值
                limit = (limit.isEmpty() || Integer.parseInt(limit) > 100) ? "20" : limit;
                page = (page.isEmpty()) ? "1" : page;
                if (order.isBlank()) {
                    order = "id ASC";
                } else {
                    order = "id " + order;
                }
                log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
                //对于type值进行判断
                List<CarbonTradeDO> getTradeList;
                switch (type) {
                    case "all" ->
                            getTradeList = carbonDAO.getTradeListAll(getUuid, Integer.valueOf(limit), Integer.valueOf(page), order);
                    case "draft", "active", "completed", "cancelled" ->
                            getTradeList = carbonDAO.getTradeListByStatus(getUuid, search, Integer.valueOf(limit), Integer.valueOf(page), order);
                    case "search" ->
                            getTradeList = carbonDAO.getTradeListBySearch(getUuid, search, Integer.valueOf(limit), Integer.valueOf(page), order);
                    default -> {
                        return ResultUtil.error(timestamp, "type参数错误", ErrorCode.REQUEST_BODY_ERROR);
                    }
                }
                //整理数据
                ArrayList<BackCarbonTradeListVO> backCarbonTradeList=new ArrayList<>();
                if (getTradeList != null) {
                    for (CarbonTradeDO getTrade : getTradeList) {
                        BackCarbonTradeListVO backCarbonTradeListVO = new BackCarbonTradeListVO();
                        BackUserVO backUserVO = new BackUserVO();
                        backUserVO.setUuid(getUuid)
                                .setUserName(getUser.getUserName())
                                .setNickName(getUser.getNickName())
                                .setRealName(getUser.getRealName())
                                .setEmail(getUser.getEmail())
                                .setPhone(getUser.getPhone())
                                .setCreatedAt(getUser.getCreatedAt())
                                .setUpdatedAt(getUser.getUpdatedAt());
                        backCarbonTradeListVO.setOrganize(backUserVO)
                                .setQuotaAmount(getTrade.getQuotaAmount().toString())
                                .setPricePerUnit(getTrade.getPricePerUnit().toString())
                                .setDescription(getTrade.getDescription());
                        backCarbonTradeList.add(backCarbonTradeListVO);
                    }
                    //输出
                    return ResultUtil.success(timestamp,"您的所需组织碳交易发布信息列表已准备完毕",backCarbonTradeList);
                } else {
                    return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "您未发布碳交易", ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
            }
        } else {
            return ResultUtil.error(timestamp, "未查询到组长账号", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> reviewTradeList(long timestamp, @NotNull HttpServletRequest request, @NotNull String tradeId) {
        UserDO getUser = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUser != null) {
            CarbonTradeDO getCarbonTradeDO = carbonDAO.getTradeById(tradeId);
            if (getCarbonTradeDO != null) {
                getCarbonTradeDO
                        .setVerifyUuid(getUser.getUuid())
                        .setStatus("active");
                if (carbonDAO.reviewTrade(getCarbonTradeDO)) {
                    return ResultUtil.success(timestamp, "审核通过");
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.UPDATE_DATA_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "交易不存在", ErrorCode.TRANSACTION_REVIEW_FAILED);
            }
        } else {
            return ResultUtil.error(timestamp, "未查询到组织账号", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }
}
