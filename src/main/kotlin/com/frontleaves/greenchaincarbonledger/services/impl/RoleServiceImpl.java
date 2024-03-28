package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.common.BusinessConstants;
import com.frontleaves.greenchaincarbonledger.dao.PermissionDAO;
import com.frontleaves.greenchaincarbonledger.dao.RoleDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.mappers.PermissionMapper;
import com.frontleaves.greenchaincarbonledger.mappers.RoleMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.RoleVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackRoleCurrentVO;
import com.frontleaves.greenchaincarbonledger.services.RoleService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.frontleaves.greenchaincarbonledger.utils.redis.RoleRedis;
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
 * AuthServiceImpl
 * <hr/>
 * 用于用户的认证服务
 *
 * @author DC_DC
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.RoleService
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final Gson gson;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final RoleRedis roleRedis;
    private final PermissionDAO permissionDAO;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addRole(long timestamp, @NotNull HttpServletRequest request, @NotNull RoleVO roleVO) {
        log.info("[Service] 执行 addRole 方法");
        //用缓存的UUID与数据库UUID进行校对
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        if (getUserDO != null) {
            // 判断角色名是否和数据库fy_role中有重复
            RoleDO roleDO = roleDAO.getRoleByName(roleVO.getName());
            if (roleDO == null) {
                ArrayList<String> arrayList1 = roleVO.getPermission();
                ArrayList<String> arrayList2 = new ArrayList<>(permissionMapper.getPermissionByName());
                for (String s : arrayList1) {
                    if (!(arrayList2.contains(s))) {
                        return ResultUtil.error(timestamp, "权限无效", ErrorCode.REQUEST_BODY_ERROR);
                    }
                }
                String uuid = ProcessingUtil.createUuid();
                // 验证成功后，向数据库中添加信息
                String json = gson.toJson(roleVO.getPermission());
                if (roleMapper.insertRole(uuid, roleVO.getName(), roleVO.getDisplayName(), json, getUuid)) {
                    return ResultUtil.success(timestamp, "角色信息已添加");
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "角色名重复", ErrorCode.REQUEST_BODY_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.UUID_NOT_EXIST);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getUserCurrent(long timestamp, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 getUserCurrent 方法");
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
                backRoleCurrent.setUuid(getUserRole.getUuid()).setName(getUserRole.getName()).setDisplayName(getUserRole.getDisplayName()).setPermission(getPermissionList);
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
    public ResponseEntity<BaseResponse> editRole(long timestamp, @NotNull HttpServletRequest request, @NotNull RoleVO roleVO, @NotNull String roleUuid) {
        log.info("[Service] 执行 editRole 方法");
        //用缓存的UUID与数据库UUID进行校对
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        if (getUserDO != null) {
            // 判断角色UUID是否和数据库fy_role中有重复
            RoleDO roleDO = roleDAO.getRoleByUuid(getUuid);
            if (roleDO == null) {
                ArrayList<String> arrayList1 = roleVO.getPermission();
                ArrayList<String> arrayList2 = new ArrayList<>(permissionMapper.getPermissionByName());
                for (String s : arrayList1) {
                    if (!(arrayList2.contains(s))) {
                        return ResultUtil.error(timestamp, "权限无效", ErrorCode.REQUEST_BODY_ERROR);
                    }
                }
                // 判断角色名不重复、角色权限存在且有效
                String json = gson.toJson(roleVO.getPermission());
                if (roleMapper.updateRole(roleVO.getName(), roleVO.getDisplayName(), json, roleUuid)) {
                    return ResultUtil.success(timestamp, "修改角色信息成功");
                } else {
                    return ResultUtil.error(timestamp, ErrorCode.UPDATE_DATA_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "角色名重复", ErrorCode.REQUEST_BODY_ERROR);
            }

        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }

    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getRoleList(
            long timestamp, @NotNull HttpServletRequest request,
            @NotNull String type, String search, @NotNull String limit, @NotNull String page, String order
    ) {
        log.info("[Service] 执行 getRoleList 方法");
        // 检查参数，如果未设置（即为null），则使用默认值
        limit = (limit.isEmpty() || Integer.parseInt(limit) > 100) ? "20" : limit;
        page = (page.isEmpty()) ? "1" : page;
        if (order == null || order.isBlank()) {
            order = "ASC";
        }
        log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
        //开始进行type的值的判断
        List<RoleDO> getRoleList;
        switch (type) {
            case "all" -> {
                order = "id " + order;
                getRoleList = roleDAO.getRoleByAllList(Integer.valueOf(limit), Integer.valueOf(page), order);
            }
            case "search" -> {
                order = "id " + order;
                getRoleList = roleDAO.getRoleByFuzzy(search, Integer.valueOf(limit), Integer.valueOf(page), order);
            }
            case "permission" -> {
                order = "pid " + order;
                //首先去Permission表中模糊查询得到Name
                List<String> getNameListByPermission = permissionDAO.getNameBySearch(search, order);
                getRoleList = new ArrayList<>();
                for (String namePermission : getNameListByPermission) {
                    ArrayList<RoleDO> getRoleDO = (ArrayList<RoleDO>) roleDAO.getRoleByPermissionName(namePermission);
                    // 这里的链表只存role数据
                    for (RoleDO roleDO : getRoleDO) {
                        if (!getRoleList.contains(roleDO)) {
                            getRoleList.add(roleDO);
                        }
                    }
                }
                int getPageLimit = Integer.parseInt(limit) * Integer.parseInt(page);
                if (!getRoleList.isEmpty() && getRoleList.size() >= getPageLimit) {
                    getRoleList = getRoleList.subList(Integer.parseInt(limit) * (Integer.parseInt(page) - 1), getPageLimit);
                } else {
                    getRoleList = new ArrayList<>();
                }
            }
            case "user" -> {
                order = "uid " + order;
                //首先去User表中模糊查询得到role链表
                //这里的链表只存role数据
                List<String> getRoleListByUser = userDAO.getRoleByAllList(search, Integer.valueOf(limit), Integer.valueOf(page), order);
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
                return ResultUtil.error(timestamp, "type 参数有误", ErrorCode.REQUEST_BODY_ERROR);
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

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> deleteRole(long timestamp, @NotNull HttpServletRequest request, @NotNull String roleUuid) {
        log.info("[Service] 执行 deleteRole 方法");
        //用缓存的UUID与数据库UUID进行校对
        RoleDO roleDO = roleDAO.getRoleByUuid(roleUuid);
        ArrayList<String> arrayList = new ArrayList<>(List.of("default", "organize", "admin", "console"));
        if (roleDO != null) {
            if (!arrayList.contains(roleDO.getName())) {
                // 进行删除操作
                if (roleMapper.deleteRole(roleUuid)) {
                    roleRedis.delData(BusinessConstants.NONE, roleDO.uuid);
                    return ResultUtil.success(timestamp, "角色信息删除成功");
                } else {
                    return ResultUtil.error(timestamp, "角色信息删除失败", ErrorCode.USER_NOT_EXISTED);
                }
            } else {
                return ResultUtil.error(timestamp, ErrorCode.ROLE_CANNOT_BE_DELETED);
            }
        } else {
            return ResultUtil.error(timestamp, "角色不存在", ErrorCode.UUID_NOT_EXIST);
        }
    }
}
