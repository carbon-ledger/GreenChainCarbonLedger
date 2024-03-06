package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.PermissionDAO;
import com.frontleaves.greenchaincarbonledger.dao.RoleDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackRoleCurrentVO;
import com.frontleaves.greenchaincarbonledger.services.RoleService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 123
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final Gson gson;
    private final PermissionDAO permissionDAO;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getUserCurrent(long timestamp, @NotNull HttpServletRequest request) {
        //用缓存的UUID与数据库UUID进行校对
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        if (getUserDO != null) {
            //这里已经获取了用户此时的Role
            RoleDO getUserRole = roleDAO.getRoleByUuid(getUserDO.getRole());
            //判断用户此时Role是否获取成功
            if (getUserRole != null) {
                //现在进行值的输出，先将其存入返回值VO里面
                //先对取出来的Permission进行解析并且放入链表里面
                ArrayList<String> getPermissionList = gson.fromJson(getUserDO.getPermission(), new TypeToken<ArrayList<String>>() {
                }.getType());
                if (getPermissionList == null) {
                    getPermissionList = new ArrayList<>();
                }
                BackRoleCurrentVO backRoleCurrent = new BackRoleCurrentVO();
                //将DO数据传入VO中
                backRoleCurrent.setUuid(getUserRole.getUuid())
                        .setName(getUserRole.getName())
                        .setDisplayName(getUserRole.getDisplayName())
                        .setPermission(getPermissionList);
                //数据进行输出
                return ResultUtil.success(timestamp, "当前角色信息已准备完毕", backRoleCurrent);
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }

    }

    @NotNull
    @Override
    //TODO:管理员权限注解
    public ResponseEntity<BaseResponse> getRoleList(long timestamp, @NotNull HttpServletRequest request, @NotNull String type, String search, Integer limit, Integer page, String order) {
        log.info("[Service] 执行getRoleList");
        // 检查参数，如果未设置（即为null），则使用默认值
        limit = (limit == null || limit > 100) ? 20 : limit;
        page = (page == null) ? 1 : page;
        if (order == null || order.isBlank()) {
            order = "uuid ASC";
        } else {
            order = "uuid " + order;
        }
        log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
        //开始进行type的值的判断
        List<RoleDO> getRoleList;
        switch (type) {
            case "all" -> getRoleList = roleDAO.getRoleByAllList(limit, page, order);
            case "search" -> getRoleList = roleDAO.getRoleByFuzzy(search, limit, page, order);
            case "permission" -> {
                //首先去Permission表中模糊查询得到Name
                List<String> getNameListByPermission = permissionDAO.getNameBySearch(search, limit, page, order);
                getRoleList = new ArrayList<>();
                for (String namePermission : getNameListByPermission) {
                    RoleDO getRoleDO = roleDAO.getRoleByPermissionName(namePermission);
                    if (!getRoleList.contains(getRoleDO)) {
                        getRoleList.add(getRoleDO);
                    }
                }
            }
            case "user" -> {
                //首先去User表中模糊查询得到role链表
                //这里的链表只存role数据
                List<String> getRoleListByUser = userDAO.getRoleByAllList(search, limit, page, order);
                getRoleList = new ArrayList<>();
                for (String roleUuid : getRoleListByUser) {
                    //已经把role提出
                    RoleDO getRoleDO = roleDAO.getRoleByUuid(roleUuid);
                    if (!getRoleList.contains(getRoleDO)) {
                        getRoleList.add(getRoleDO);
                    }
                }
            }
            default -> {
                return ResultUtil.error(timestamp, "type 参数有误", ErrorCode.QUERY_PARAM_ERROR);
            }
        }
        // 整理数据
        ArrayList<BackRoleCurrentVO> backRoleCurrentList = new ArrayList<>();
        for (RoleDO getRole : getRoleList) {
            BackRoleCurrentVO backRoleCurrentVO = new BackRoleCurrentVO();
            backRoleCurrentVO.setName(getRole.getName());
            backRoleCurrentVO.setUuid(getRole.getUuid());
            backRoleCurrentVO.setDisplayName(getRole.getDisplayName());
            backRoleCurrentVO.setPermission(gson.fromJson(getRole.getPermission(), new TypeToken<ArrayList<String>>() {
            }.getType()));
            backRoleCurrentList.add(backRoleCurrentVO);
        }
        return ResultUtil.success(timestamp, "角色列表信息已经准备完毕", backRoleCurrentList);
    }
}
