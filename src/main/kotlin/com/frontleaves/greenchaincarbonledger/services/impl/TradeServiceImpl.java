package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.*;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.*;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonBuyTradeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonTradeListVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackOpenAnAccount;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackUserVO;
import com.frontleaves.greenchaincarbonledger.services.TradeService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.hyperledger.fabric.gateway.Contract;
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
    private final CarbonTradeDAO carbonTradeDAO;
    private final CarbonQuotaDAO carbonQuotaDAO;
    private final ApproveDAO approveDAO;
    private final Gson gson;
    //private final Contract contract;
    private final CarbonMapper carbonMapper;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> releaseCarbonTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull TradeReleaseVO tradeReleaseVO) {
        log.info("[Service] 执行 releaseCarbonTrade 方法");
        String getUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        // 先对自己组织剩余的碳配额量进行判断
        // 1.获取 总配额量total_quota、已分配额量allocated_quota、已使用配额量used_quota
        CarbonQuotaDO carbonQuotaDO = carbonDAO.getOrganizeQuotaByUuid(getUuid);
        if (carbonQuotaDO != null) {
            // 获取年份
            if (new SimpleDateFormat("yyyy").format(timestamp).equals(carbonQuotaDO.getQuotaYear().toString())) {
                double totalQuota = carbonQuotaDO.getTotalQuota();
                double allocatedQuota = carbonQuotaDO.getAllocatedQuota();
                double usedQuota = carbonQuotaDO.getUsedQuota();
                // 2.根据三个数据获取组织现有的碳配额量
                double nowQuota = totalQuota - usedQuota - Integer.parseInt(tradeReleaseVO.getAmount());
                // 3.如果企业的 已使用配额量used_quota 小于 已分配额量allocated_quota 的话，才允许发布碳交易
                // 达到允许条件下，则可发布交易
                if (nowQuota > 0 && allocatedQuota > usedQuota) {
                    // 对发布的内容进行区块链上链
                    String blockchainID = ProcessingUtil.createUuid();
                    byte[] getByte = null;
//                    try {
//                        contract.submitTransaction("createContract", blockchainID, String.valueOf(carbonMapper.getLastThird().getId() + 1), carbonQuotaDO.getOrganizeUuid(), tradeReleaseVO.getAmount(), tradeReleaseVO.getUnit());
//                        getByte = contract.evaluateTransaction("queryTrade", blockchainID);
//                    } catch (Exception e) {
//                        log.error("[Service] 区块链上链失败", e);
//                    }
                    if (tradeReleaseVO.getDraft()) {
                        carbonMapper.insertTradeByUuid(getUuid, tradeReleaseVO.getAmount(), tradeReleaseVO.getUnit(), tradeReleaseVO.getText(), blockchainID, "draft");
                    } else {
                        carbonMapper.insertTradeByUuid(getUuid, tradeReleaseVO.getAmount(), tradeReleaseVO.getUnit(), tradeReleaseVO.getText(), blockchainID, "pending_review");
                    }
                    // 添加审计日志
                    ArrayList<AuditLogDO> auditLogDOList = gson.fromJson(carbonQuotaDO.getAuditLog(), new TypeToken<List<AuditLogDO>>() {
                    }.getType());
                    carbonQuotaDO.setAuditLog(gson.toJson(
                            ProcessingUtil.addAuditLog(
                                    auditLogDOList,
                                    "来自交易扣除，扣除配额：" + tradeReleaseVO.getAmount() + "吨",
                                    "系统交易操作")
                    ));
                    carbonDAO.addAuditLog(carbonQuotaDO.getUuid(), carbonQuotaDO.getAuditLog());
                    // 交易发布后扣除当前可用碳
                    carbonDAO.changeTotalQuota(carbonQuotaDO.getUuid(), totalQuota - Integer.parseInt(tradeReleaseVO.getAmount()));
                    return ResultUtil.success(timestamp, "交易发布成功", Arrays.toString(getByte));
                } else {
                    if (nowQuota <= 0) {
                        return ResultUtil.error(timestamp, "组织剩余配额不足", ErrorCode.RELEASE_TRADE_FAILURE);
                    } else {
                        return ResultUtil.error(timestamp, "无法使用购入的碳配额量进行交易", ErrorCode.RELEASE_TRADE_FAILURE);
                    }
                }
            }
        }
        return ResultUtil.error(timestamp, "您还未申请碳配额", ErrorCode.RELEASE_TRADE_FAILURE);
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> deleteTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull String id) {
        log.info("[Service] 执行 changeStatus 方法");
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
                    if (id.equals(carbonTradeDO.getId().toString())) {
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
                    //进行时间格式转换
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date date = sdf.parse(getCarbonTrade.getCreatedAt());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int buyYear = calendar.get(Calendar.YEAR);
                        if (buyYear != localYear) {
                            return ResultUtil.error(timestamp, "您没有权限删除过时的订单", ErrorCode.NO_PERMISSION_ERROR);
                        } else {//校验要删除的ID的status是否为completed
                            if ("completed".equals(getCarbonTrade.getStatus())) {
                                return ResultUtil.error(timestamp, "无法删除已完成交易的碳核算交易", ErrorCode.NO_PERMISSION_ERROR);
                            } else {
                                if ("cancelled".equals(getCarbonTrade.getStatus())) {
                                    return ResultUtil.error(timestamp, "请勿重复删除", ErrorCode.DUPLICATE_DELETION);
                                } else {
                                    String status = "cancelled";
                                    Boolean result = carbonDAO.changeStatus(id, status);
                                    if (result) {
                                        log.debug("[Service] 数据库软删除更新数据");
                                        //获取用户的碳排放额
                                        CarbonQuotaDO getCarbonQuota = carbonQuotaDAO.getCarbonQuota(localYear, getAuthUserDO.getUuid());
                                        //进行碳交易碳总量的返还
                                        Double nowBuyTotalQuota = getCarbonTrade.getQuotaAmount() + getCarbonQuota.getTotalQuota();
                                        ArrayList<AuditLogDO> auditLogDOList = gson.fromJson(getCarbonQuota.getAuditLog(), new TypeToken<List<AuditLogDO>>() {
                                        }.getType());
                                        getCarbonQuota.setAuditLog(gson.toJson(
                                                ProcessingUtil.addAuditLog(
                                                        auditLogDOList,
                                                        "来自交易添取消返还，返还配额：" + getCarbonTrade.getQuotaAmount() + "吨",
                                                        "系统交易操作")
                                        ));
                                        carbonDAO.addAuditLog(getCarbonQuota.getUuid(), getCarbonQuota.getAuditLog());
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
                    } catch (ParseException e) {
                        log.error("时间解析错误", e);
                        return ResultUtil.error(timestamp, "订单时间解析错误", ErrorCode.PARSING_TIME_ERROR);
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
            List<CarbonTradeDO> getOwnTradeList = carbonTradeDAO.getTradeListByUuid(getUuid);
            if (getOwnTradeList != null && !getOwnTradeList.isEmpty()) {
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
                        ArrayList<String> errorCodeReturn = new ArrayList<>();
                        errorCodeReturn.add("type 参数错误");
                        return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR,errorCodeReturn);
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
                                .setTradeId(Long.valueOf(getTrade.getId()))
                                .setBuyOrganization(getTrade.getOrganizeUuid())
                                .setQuotaAmount(getTrade.getQuotaAmount().toString())
                                .setPricePerUnit(getTrade.getPricePerUnit().toString())
                                .setDescription(getTrade.getDescription())
                                .setStatus(getTrade.getStatus());
                        backCarbonTradeList.add(backCarbonTradeListVO);
                    }
                    //输出
                    return ResultUtil.success(timestamp, "您的所需组织碳交易发布信息列表已准备完毕", backCarbonTradeList);
                } else {
                    return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "您未发布碳交易", ErrorCode.CAN_T_PUBLISH_TRADE);
            }
        } else {
            return ResultUtil.error(timestamp, "未查询到组织账号", ErrorCode.UUID_NOT_EXIST);
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
                                        if (carbonDAO.changeStatus(id, "trade")) {
                                            //整理数据
                                            BackCarbonBuyTradeVO backCarbonBuyTrade = new BackCarbonBuyTradeVO();
                                            BackUserVO backUserVO = new BackUserVO();
                                            // 获取交易组织
                                            UserDO getSendOrganizeDO = userDAO.getUserByUuid(carbonTradeDO.getOrganizeUuid());
                                            backUserVO
                                                    .setUuid(getSendOrganizeDO.getUuid())
                                                    .setUserName(getSendOrganizeDO.getUserName())
                                                    .setNickName(getSendOrganizeDO.getNickName())
                                                    .setRealName(getSendOrganizeDO.getRealName())
                                                    .setEmail(getSendOrganizeDO.getEmail())
                                                    .setPhone(getSendOrganizeDO.getPhone())
                                                    .setCreatedAt(getSendOrganizeDO.getCreatedAt())
                                                    .setUpdatedAt(getSendOrganizeDO.getUpdatedAt());
                                            backCarbonBuyTrade.setOrganize(backUserVO)

                                                    .setQuotaAmount(carbonTradeDO.getQuotaAmount().toString())
                                                    .setPricePerUnit(carbonTradeDO.getPricePerUnit().toString())
                                                    .setDescription(carbonTradeDO.getDescription());
                                            // 添加购买人
                                            carbonDAO.setTradeBuyUuid(id, getOrganizeDO.getUuid());
                                            return ResultUtil.success(timestamp, "您已完成碳交易", backCarbonBuyTrade);
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
                    } else {
                        return ResultUtil.error(timestamp, "抱歉不可以购买自己发布的订单", ErrorCode.ILLEGAL_PURCHASES);
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
            @NotNull String order
    ) {
        log.info("[Service] 执行 getTradeList 方法");
        UserDO getUser = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUser != null) {
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
                    ArrayList<String> errorCodeReturn = new ArrayList<>();
                    errorCodeReturn.add("type 参数错误");
                    return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR,errorCodeReturn);
                }
            }
            ArrayList<BackCarbonTradeListVO> backCarbonTradeList = new ArrayList<>();
            if (getTradeList != null) {
                for (CarbonTradeDO getTrade : getTradeList) {
                    BackCarbonTradeListVO backCarbonTradeListVO = new BackCarbonTradeListVO();
                    BackUserVO backUserVO = new BackUserVO();
                    // 获取组织账户
                    UserDO getOrganizeDO = userDAO.getUserByUuid(getTrade.getOrganizeUuid());
                    backUserVO
                            .setUuid(getOrganizeDO.getUuid())
                            .setUserName(getOrganizeDO.getUserName())
                            .setRealName(getOrganizeDO.getRealName())
                            .setEmail(getOrganizeDO.getEmail())
                            .setAvatar(getOrganizeDO.getAvatar())
                            .setPhone(getOrganizeDO.getPhone())
                            .setCreatedAt(getOrganizeDO.getCreatedAt())
                            .setUpdatedAt(getOrganizeDO.getUpdatedAt());
                    backCarbonTradeListVO.setOrganize(backUserVO)
                            .setTradeId(Long.valueOf(getTrade.getId()))
                            .setStatus(getTrade.getStatus())
                            .setQuotaAmount(getTrade.getQuotaAmount().toString())
                            .setPricePerUnit(getTrade.getPricePerUnit().toString())
                            .setDescription(getTrade.getDescription());
                    // 若已完成交易则获取买方
                    if (getTrade.getStatus().equals("completed")) {
                        UserDO getBuyUserDO = userDAO.getUserByUuid(getTrade.getBuyUuid());
                        backCarbonTradeListVO.setBuyOrganization(getBuyUserDO.getRealName());
                    }
                    backCarbonTradeList.add(backCarbonTradeListVO);
                }
                //输出
                return ResultUtil.success(timestamp, "您的所需组织碳交易发布信息列表已准备完毕", backCarbonTradeList);
            } else {
                return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, "未查询到组长账号", ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> reviewTradeList(long timestamp, @NotNull HttpServletRequest request, @NotNull String tradeId, boolean pass) {
        log.info("[Service] 执行 reviewTradeList 方法");
        UserDO getUser = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUser != null) {
            CarbonTradeDO getCarbonTradeDO = carbonDAO.getTradeById(tradeId);
            if (getCarbonTradeDO != null) {
                getCarbonTradeDO
                        .setVerifyUuid(getUser.getUuid());
                if (pass) {
                    getCarbonTradeDO.setStatus("active");
                } else {
                    getCarbonTradeDO.setStatus("draft");
                }
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

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> editCarbonTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull TradeReleaseVO tradeReleaseVO, @NotNull String id) {
        log.info("[Service] 执行 editCarbonTrade 方法");
        String getUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        // 判断用户是否发布过交易
        // 判断交易是否已经发布

        CarbonTradeDO carbonTradeDO = carbonDAO.getTradeByUuidAndId(getUuid, id);
        String status = carbonTradeDO.getStatus();
        if ("draft".equals(status) || "pending_review".equals(status)) {
            // 判断编辑的信息是否合法有效，如果有效则可以提交编辑
            if (tradeReleaseVO.getDraft()) {
                carbonTradeDAO.editTrade(getUuid, tradeReleaseVO, "draft", id);
            } else {
                carbonTradeDAO.editTrade(getUuid, tradeReleaseVO, "pending_review", id);
            }
            return ResultUtil.success(timestamp, "交易发布信息修改成功");
        } else {
            return ResultUtil.error(timestamp, ErrorCode.EDIT_TRADE_FAILURE);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getBuyTradeList(long timestamp, @NotNull HttpServletRequest request) {
        // 获取当前用户
        UserDO getUser = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUser != null) {
            // 获取此用户进行的所有碳交易信息
            List<CarbonTradeDO> getTradeList = carbonDAO.getBuyTradeListByUuid(getUser.getUuid());
            if (getTradeList != null) {
                // 遍历获取到的所有碳交易信息
                ArrayList<BackCarbonTradeListVO> backCarbonTradeList = new ArrayList<>();
                for (CarbonTradeDO getTrade : getTradeList) {
                    BackCarbonTradeListVO backCarbonTradeListVO = new BackCarbonTradeListVO();
                    BackUserVO backUserVO = new BackUserVO();
                    // 获取对方交易的信息
                    UserDO getOrganizeDO = userDAO.getUserByUuid(getTrade.getOrganizeUuid());
                    backUserVO
                            .setUuid(getOrganizeDO.getUuid())
                            .setUserName(getOrganizeDO.getUserName())
                            .setRealName(getOrganizeDO.getRealName())
                            .setNickName(getOrganizeDO.getNickName())
                            .setEmail(getOrganizeDO.getEmail())
                            .setPhone(getOrganizeDO.getPhone())
                            .setCreatedAt(getOrganizeDO.getCreatedAt())
                            .setUpdatedAt(getOrganizeDO.getUpdatedAt());
                    backCarbonTradeListVO.setOrganize(backUserVO)
                            .setTradeId(Long.valueOf(getTrade.getId()))
                            .setQuotaAmount(getTrade.getQuotaAmount().toString())
                            .setPricePerUnit(getTrade.getPricePerUnit().toString())
                            .setDescription(getTrade.getDescription())
                            .setStatus(getTrade.getStatus());
                    backCarbonTradeList.add(backCarbonTradeListVO);
                }
                return ResultUtil.success(timestamp, "您的所需组织碳交易发布信息列表已准备完毕", backCarbonTradeList);
            } else {
                return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, "未查询到组织账号", ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getReviewTradeList(long timestamp, @NotNull HttpServletRequest request) {
        // 获取需要审核的列表
        List<CarbonTradeDO> getTradeList = carbonDAO.getTradeNeedReview();
        if (getTradeList != null) {
            // 遍历获取到的所有碳交易信息
            ArrayList<BackCarbonTradeListVO> backCarbonTradeList = new ArrayList<>();
            for (CarbonTradeDO getTrade : getTradeList) {
                BackCarbonTradeListVO backCarbonTradeListVO = new BackCarbonTradeListVO();
                BackUserVO backUserVO = new BackUserVO();
                UserDO getUser = userDAO.getUserByUuid(getTrade.getOrganizeUuid());
                backUserVO.setUuid(getTrade.getOrganizeUuid())
                        .setUserName(getUser.getUserName())
                        .setNickName(getUser.getNickName())
                        .setRealName(getUser.getRealName())
                        .setEmail(getUser.getEmail())
                        .setPhone(getUser.getPhone())
                        .setCreatedAt(getUser.getCreatedAt())
                        .setUpdatedAt(getUser.getUpdatedAt());
                backCarbonTradeListVO.setOrganize(backUserVO)
                        .setTradeId(Long.valueOf(getTrade.getId()))
                        .setQuotaAmount(getTrade.getQuotaAmount().toString())
                        .setPricePerUnit(getTrade.getPricePerUnit().toString())
                        .setDescription(getTrade.getDescription())
                        .setStatus(getTrade.getStatus());
                backCarbonTradeList.add(backCarbonTradeListVO);
            }
            return ResultUtil.success(timestamp, "您的所需组织碳交易发布信息列表已准备完毕", backCarbonTradeList);
        } else {
            return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getTradeBank(long timestamp, @NotNull HttpServletRequest request, @NotNull String tradeId) {
        // 查询交易 tradeId
        CarbonTradeDO getTrade = carbonDAO.getTradeById(tradeId);
        if (getTrade != null) {
            // 检查是否有权限查看交易
            if (getTrade.getBuyUuid() != null) {
                if (getTrade.getBuyUuid().equals(ProcessingUtil.getAuthorizeUserUuid(request))) {
                    // 获取组织信息
                    UserDO getUser = userDAO.getUserByUuid(getTrade.getOrganizeUuid());
                    if (getUser != null) {
                        // 获取组织账户信息
                        ApproveOrganizeDO approveOrganizeDO = approveDAO.getOrganizeAccountByUuid(getTrade.getOrganizeUuid());
                        // 处理返回数据
                        if (approveOrganizeDO != null) {
                            // 返回开户行相关信息以及对方账户的用户信息
                            BackOpenAnAccount backOpenAnAccount = new BackOpenAnAccount();
                            backOpenAnAccount.setOrganize(new BackUserVO()
                                    .setUuid(getUser.getUuid())
                                    .setUserName(getUser.getUserName())
                                    .setNickName(getUser.getNickName())
                                    .setRealName(getUser.getRealName())
                                    .setEmail(getUser.getEmail())
                                    .setPhone(getUser.getPhone())
                                    .setCreatedAt(getUser.getCreatedAt())
                                    .setUpdatedAt(getUser.getUpdatedAt()));
                            backOpenAnAccount.setAccountOpen(new BackOpenAnAccount.AccountOpen()
                                    .setAccountBank(approveOrganizeDO.getAccountBank())
                                    .setAccountNumber(approveOrganizeDO.getAccountNumber()));
                            return ResultUtil.success(timestamp, "您的所需组织碳交易发布信息列表已准备完毕", backOpenAnAccount);
                        } else {
                            return ResultUtil.error(timestamp, "未能查询到组织账户余额", ErrorCode.SERVER_INTERNAL_ERROR);
                        }
                    } else {
                        return ResultUtil.error(timestamp, "未能查询到组织信息", ErrorCode.SERVER_INTERNAL_ERROR);
                    }
                } else {
                    return ResultUtil.error(timestamp, "您没有权限查看该组织信息", ErrorCode.USER_CANNOT_BE_OPERATE);
                }
            } else {
                return ResultUtil.error(timestamp, "交易还未达成", ErrorCode.USER_CANNOT_BE_OPERATE);
            }
        } else {
            return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> checkTradeSuccess(long timestamp, @NotNull HttpServletRequest request, @NotNull String tradeId) {
        log.info("[Service] 执行 checkTradeSuccess 方法");
        // 获取交易 ID
        CarbonTradeDO getTrade = carbonDAO.getTradeById(tradeId);
        if (getTrade != null) {
            // 交易需要在 trade 模式
            if (getTrade.getStatus().equals("trade")) {
                // 获取组织账户信息
                ApproveOrganizeDO approveOrganizeDO = approveDAO.getOrganizeAccountByUuid(getTrade.getOrganizeUuid());
                // 完成收款，操作对方账户添加碳配额
                if (approveOrganizeDO != null) {
                    // 获取对方碳配额
                    CarbonQuotaDO getQuota = carbonDAO.getOrganizeQuotaByUuid(getTrade.getBuyUuid());
                    // 操作对方账户添加碳配额
                    if (getQuota != null) {
                        // 添加碳配额
                        getQuota.setTotalQuota(getQuota.getTotalQuota() + getTrade.getQuotaAmount());
                        // 更新数据库
                        carbonDAO.changeTotalQuota(getQuota.getUuid(), getQuota.getTotalQuota());
                        // 添加审计日志
                        ArrayList<AuditLogDO> auditLogDOList = gson.fromJson(getQuota.getAuditLog(), new TypeToken<List<AuditLogDO>>() {
                        }.getType());
                        getQuota.setAuditLog(gson.toJson(
                                ProcessingUtil.addAuditLog(
                                        auditLogDOList,
                                        "来自交易添加，交易ID：" + getTrade.getId() + "，添加配额：" + getTrade.getQuotaAmount(),
                                        "系统交易操作")
                        ));
                        carbonDAO.addAuditLog(getQuota.getUuid(), getQuota.getAuditLog());
                        // 修改自己状态
                        carbonDAO.changeStatus(tradeId, "completed");
                        return ResultUtil.success(timestamp, "交易已完成");
                    } else {
                        return ResultUtil.error(timestamp, "未能查询到对方账户信息", ErrorCode.SERVER_INTERNAL_ERROR);
                    }
                } else {
                    return ResultUtil.error(timestamp, "未能查询到组织账户信息", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "交易状态错误", ErrorCode.STATUS_NON_COMPLIANCE);
            }
        } else {
            return ResultUtil.error(timestamp, "未能查询到数据", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }
}
