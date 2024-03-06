package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.RoleDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackUserCurrentVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackDesensitizationVO;
import com.frontleaves.greenchaincarbonledger.services.UserService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类，提供处理用户相关业务逻辑的方法。
 * <hr/>
 * 该服务实现类包括获取当前登录用户的个人信息等功能。用户在成功登录后，可以通过调用相应的方法来获取自己的账户详细信息，
 * 包括姓名、联系方式、电子邮件地址等。通常用于个人资料页面，允许用户查看其信息。
 *
 * @author FLASHALCK
 * @version 1.0
 * @since 2024-4-1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Override
    public @NotNull ResponseEntity<BaseResponse> getUserCurrent(long timestamp, HttpServletRequest request) {
        //用缓存的UUID与数据库UUID进行校对
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        //获取自己的账号详细信息（姓名，联系方式，电子邮件等）
        if (getUserDO != null) {
            ArrayList<String> getPermissionList = gson.fromJson(getUserDO.getPermission(), new TypeToken<ArrayList<String>>() {
            }.getType());
            if (getPermissionList == null) {
                getPermissionList = new ArrayList<>();
            }
            // 数据整理
            BackUserCurrentVO backUserCurrent = new BackUserCurrentVO();
            BackUserCurrentVO.UserVO newUserInfo = new BackUserCurrentVO.UserVO();
            BackUserCurrentVO.PermissionVO newPermissionInfo = new BackUserCurrentVO.PermissionVO();
            //  如果是进行了脱敏处理，就可以创建一个新的工具类进行统一处理
            newUserInfo.setUserName(getUserDO.getUserName()).setRealName(getUserDO.getRealName()).setEmail(getUserDO.getEmail()).setPhone(getUserDO.getPhone()).setUuid(getUserDO.getUuid());
            // TODO: 权限信息写好后，需要数据库调取
            newPermissionInfo.setUserPermission(getPermissionList).setRolePermission(getPermissionList);

            backUserCurrent.setUser(newUserInfo).setPermission(newPermissionInfo).setRole(roleDAO.getRoleByUuid(getUserDO.getRole()).getName());
            // 数据输出
            return ResultUtil.success(timestamp, "用户查看的信息已准备完毕", backUserCurrent);
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    //TODO:还未进行权限验证（接口未写好）
    public ResponseEntity<BaseResponse> getUserList(long timestamp, @NotNull HttpServletRequest request, @NotNull String type, String search, Integer limit, Integer page, String order) {
        log.info("[Service] 执行 getUserList 方法");
        // 检查参数，如果未设置（即为null），则使用默认值
        limit = (limit == null || limit > 100) ? 20 : limit;
        page = (page == null) ? 1 : page;
        if (order == null || order.isBlank()) {
            order = "uid ASC";
        } else {
            order = "uuid " + order;
        }
        log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
        // 1. 对type类型进行判断
        List<UserDO> getUserDO;
        switch (type) {
            case "search" -> getUserDO = userDAO.getUserFuzzy(search, limit, page, order);
            case "unbanlist" -> getUserDO = userDAO.getUserByUnbanlist(limit, page, order);
            case "banlist" -> getUserDO = userDAO.getUserByBanlist(limit, page, order);
            case "available" -> getUserDO = userDAO.getUserByAvailablelist(limit, page, order);
            case "all" -> getUserDO = userDAO.getUserByAlllist(limit, page, order);
            default -> {
                return ResultUtil.error(timestamp, "type 参数有误",ErrorCode.QUERY_PARAM_ERROR);
            }
        }
        List<BackDesensitizationVO> desensitizationVO = modelMapper.map(getUserDO, new TypeToken<List<BackDesensitizationVO>>() {
        }.getType());
        return ResultUtil.success(timestamp, "管理员查看的信息已准备完毕", desensitizationVO);
    }
}
