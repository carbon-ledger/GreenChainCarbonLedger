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
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author 123
 */
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

    }
}
