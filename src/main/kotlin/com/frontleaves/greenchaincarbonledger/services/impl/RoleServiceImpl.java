package com.frontleaves.greenchaincarbonledger.services.impl;

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
import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.mappers.PermissionMapper;
import com.frontleaves.greenchaincarbonledger.mappers.RoleMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.RoleDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.RoleVO;
import com.frontleaves.greenchaincarbonledger.services.RoleService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final Gson gson;

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

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final Gson gson;
    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addRole(long timestamp, @NotNull HttpServletRequest request, @NotNull RoleVO roleVO) {
        //用缓存的UUID与数据库UUID进行校对
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        if (getUserDO != null){
            // 判断角色名是否和数据库fy_role中有重复
            RoleDO roleDO = roleMapper.getRoleByName(roleVO.getName());
            if (roleDO ==null){
                ArrayList<String> arrayList1 = roleVO.getPermission();
                ArrayList<String> arrayList2 = permissionMapper.getPermissionByName();
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
                } else{
                    return ResultUtil.error(timestamp, ErrorCode.INSERT_DATA_ERROR);
                }
            }
            else {
                return ResultUtil.error(timestamp, "角色名重复", ErrorCode.REQUEST_BODY_ERROR);
            }
        } else{
            return ResultUtil.error(timestamp, ErrorCode.UUID_NOT_EXIST);
        }
    }
}
