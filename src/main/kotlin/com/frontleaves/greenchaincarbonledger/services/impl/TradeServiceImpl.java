package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.CarbonDAO;
import com.frontleaves.greenchaincarbonledger.dao.CarbonQuotaDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTradeDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonBuyTradeVO;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author FLASHLACK
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {
    private final UserDAO userDAO;
    private final CarbonDAO carbonDAO;
    private final CarbonQuotaDAO carbonQuotaDAO;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> deleteTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull String id) {
        log.info("[Service] 执行 deleteTrade 方法");
        log.debug("[Service] 进行用户查询确认");
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
                    CarbonTradeDO getCarbonTrade = carbonDAO.getTradeById(id);
                    //校验删除的订单是否是今年的
                    log.debug("[Service] 时间戳获取时间");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                    int localYear = Integer.parseInt(simpleDateFormat.format(timestamp));
                    if (Integer.parseInt(simpleDateFormat.format(getCarbonTrade.getCreatedAt())) != localYear) {
                        return ResultUtil.error(timestamp, "您没有权限删除", ErrorCode.NO_PERMISSION_ERROR);
                    } else {//校验要删除的ID的status是否为completed
                        if ("completed".equals(getCarbonTrade.getStatus())) {
                            return ResultUtil.error(timestamp, "无法删除已完成交易的碳核算交易", ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                        } else {
                            if ("cancelled".equals(getCarbonTrade.getStatus())) {
                                return ResultUtil.error(timestamp, "请勿重复删除", ErrorCode.REQUEST_METHOD_NOT_SUPPORTED);
                            } else {
                                String status = "cancelled";
                                Boolean result = carbonDAO.deleteTrade(id, status);
                                if (result) {
                                    log.debug("[Service] 数据库软删除更新数据");
                                    //获取用户的碳排放额
                                    CarbonQuotaDO getCarbonQuota = carbonQuotaDAO.getCarbonQuota(localYear, getAuthUserDO.getUuid());
                                    //进行碳交易碳总量的返还
                                    Double nowBuyTotalQuota = getCarbonTrade.getQuotaAmount() + getCarbonQuota.getTotalQuota();
                                    //借用数据库更新
                                    if (carbonQuotaDAO.finishCarbonTrade(nowBuyTotalQuota, getAuthUserDO.getUuid(), localYear)) {
                                        return ResultUtil.success(timestamp, "删除成功");
                                    } else {
                                        return ResultUtil.success(timestamp, "交易已删除，但未返回配额请联系客服", ErrorCode.SERVER_INTERNAL_ERROR);
                                    }
                                } else {
                                    return ResultUtil.error(timestamp, "删除失败", ErrorCode.SERVER_INTERNAL_ERROR);
                                }
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
                log.debug("[Service] 校验参数");
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
                log.debug("[Service] 校验type");
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
                log.debug("[Service] 整理输出数据");
                //整理数据
                ArrayList<BackCarbonTradeListVO> backCarbonTradeList = new ArrayList<>();
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
                    return ResultUtil.success(timestamp, "您的所需组织碳交易发布信息列表已准备完毕", backCarbonTradeList);
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
    public ResponseEntity<BaseResponse> buyTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull String id) {
        log.info("[Service] 执行 buyTrade 方法");
        //确认买家身份并且校验是否合规
        UserDO getOrganizeDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getOrganizeDO != null) {
            log.debug("[Service] 从时间戳获取时间");
            //首先提取年份
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            int localYear = Integer.parseInt(simpleDateFormat.format(timestamp));
            //校验是否可以查到
            CarbonQuotaDO carbonQuotaDO = carbonQuotaDAO.getCarbonQuota(localYear, getOrganizeDO.getUuid());
            if (carbonQuotaDO == null) {
                return ResultUtil.error(timestamp, "抱歉未查询到您当前的碳排放额表", ErrorCode.CAN_T_ACCOUNT_FOR_CARBON);
            } else {
                //开始校验是否合规,注意此时假为合规，真为不合规(数据库与布尔值相反）
                if (carbonQuotaDO.complianceStatus) {
                    return ResultUtil.error(timestamp, "抱歉您所在的组织目前初始不合规状态", ErrorCode.STATUS_NON_COMPLIANCE);
                } else {
                    //目前买家各项符合可以进行碳交易
                    //开始查询买家购买的碳交易id是否存在和合规
                    CarbonTradeDO carbonTradeDO = carbonDAO.getTradeById(id);
                    if (carbonTradeDO != null) {
                        //进行买方和买房认证
                        if (!Objects.equals(getOrganizeDO.getUuid(), carbonTradeDO.getOrganizeUuid())) {
                            //进行合规验证
                            if ("active".equals(carbonTradeDO.getStatus())) {
                                //进行时间验证
                                //进行时间格式转换
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    //进行时间格式解析
                                    Date date = sdf.parse(carbonTradeDO.getCreatedAt());
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int buyYear = calendar.get(Calendar.YEAR);
                                    if (buyYear != localYear) {
                                        return ResultUtil.error(timestamp, "抱歉您购买的碳交易订单不在本年度", ErrorCode.ORDER_TIME_MISMATCH);
                                    } else {
                                        //进行碳交易
                                        double nowBuyTotalQuota = carbonTradeDO.getQuotaAmount() + carbonQuotaDO.getTotalQuota();
                                        if (carbonQuotaDAO.finishCarbonTrade(nowBuyTotalQuota, carbonQuotaDO.getUuid(), localYear)) {
                                            //进行删除碳交易
                                            if (carbonDAO.deleteTrade(id, "completed")) {
                                                //整理数据
                                                BackCarbonBuyTradeVO backCarbonBuyTrade = new BackCarbonBuyTradeVO();
                                                BackUserVO backUserVO = new BackUserVO();
                                                backUserVO.setUuid(getOrganizeDO.getUuid())
                                                        .setUserName(getOrganizeDO.getUserName())
                                                        .setNickName(getOrganizeDO.getNickName())
                                                        .setRealName(getOrganizeDO.getRealName())
                                                        .setEmail(getOrganizeDO.getEmail())
                                                        .setPhone(getOrganizeDO.getPhone())
                                                        .setCreatedAt(getOrganizeDO.getCreatedAt())
                                                        .setUpdatedAt(getOrganizeDO.getUpdatedAt());
                                                backCarbonBuyTrade.setOrganize(backUserVO)
                                                        .setQuotaAmount(carbonTradeDO.getQuotaAmount().toString())
                                                        .setPricePerUnit(carbonTradeDO.getPricePerUnit().toString())
                                                        .setDescription(carbonTradeDO.getDescription());
                                                //输出
                                                return ResultUtil.success(timestamp, "您已完成碳交易", backCarbonBuyTrade);
                                            } else {
                                                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                                            }
                                        } else {
                                            return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                                        }
                                    }
                                } catch (ParseException e) {
                                    log.error("解析订单时间错误", e);
                                    return ResultUtil.error(timestamp, "解析订单时间错误", ErrorCode.PARSING_TIME_ERROR);
                                }
                            } else {
                                return ResultUtil.error(timestamp, "抱歉您购买的碳交易订单不能进行交易", ErrorCode.ORDER_ILLEGAL);
                            }
                        } else {
                            return ResultUtil.error(timestamp, "抱歉您购买的碳交易订单并不存在", ErrorCode.ORDER_ILLEGAL);
                        }
                    }else {
                        return ResultUtil.error(timestamp,"抱歉不可以购买自己发布的订单",ErrorCode.ILLEGAL_PURCHASES);
                    }
                }
            }
        } else {
            return ResultUtil.error(timestamp, "未找到您的组织账号", ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getTradeList(
            long timestamp,
            @NotNull HttpServletRequest request,
            @NotNull String type,
            String search,
            @NotNull String limit,
            @NotNull String page,
            @NotNull String order) {
        UserDO getUser = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUser != null) {
            String getUuid = getUser.getUuid();
            // 此时，type参数已经被校验、page、limit仅仅验证了结构，未校验范围、order还需要赋值添加字段名
            // 转变page和limit类型
            limit = (limit.isEmpty() || Integer.parseInt(limit) > 100) ? "20" : limit;
            page = (page.isEmpty()) ? "1" : page;
            if (order.isEmpty()) {
                order = "id ASC";
            } else {
                order = "id " + order;
            }
            //对于type值进行判断
            List<CarbonTradeDO> getTradeList;
            switch (type) {
                case "all" ->
                        getTradeList = carbonDAO.getAvailableTradeListAll(Integer.valueOf(limit), Integer.valueOf(page), order);
                case "active" ->
                        getTradeList = carbonDAO.getAvailableTradeList(search, Integer.valueOf(limit), Integer.valueOf(page), order);
                case "completed" ->
                        getTradeList = carbonDAO.getCompletedTradeList(search, Integer.valueOf(limit), Integer.valueOf(page), order);
                case "search" ->
                        getTradeList = carbonDAO.getSearchTradeList(search, Integer.valueOf(limit), Integer.valueOf(page), order);
                default -> {
                    return ResultUtil.error(timestamp, "type参数错误", ErrorCode.REQUEST_BODY_ERROR);
                }
            }
            ArrayList<BackCarbonTradeListVO> backCarbonTradeList = new ArrayList<>();
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
                return ResultUtil.success(timestamp, "您的所需组织碳交易发布信息列表已准备完毕", backCarbonTradeList);
            } else {
                return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, "未查询到组长账号", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }
}
