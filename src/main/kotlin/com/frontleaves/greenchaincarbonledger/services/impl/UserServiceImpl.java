package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackUserCurrentVO;
import com.frontleaves.greenchaincarbonledger.services.UserService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    @Override
    public @NotNull ResponseEntity<BaseResponse> getUserCurrent(long timestamp, HttpServletRequest request) {
        //用缓存的UUID与数据库UUID进行校对
        String getUuid = request.getHeader("X-Auth-UUID");
        UserDO getUserDO = userDAO.getUserByUuid(getUuid);
        //获取自己的账号详细信息（姓名，联系方式，电子邮件等）
        if (getUserDO != null) {
            BackUserCurrentVO backUserCurrent = new BackUserCurrentVO();
            backUserCurrent.setUserName(getUserDO.getUserName());
            backUserCurrent.setRealName(getUserDO.getRealName());
            backUserCurrent.setEmail(getUserDO.getEmail());
            backUserCurrent.setPhone(getUserDO.getPhone());
            backUserCurrent.setPermission(getUserDO.getPermission());
            //下面需要返回数据库中角色表的UUID来区分用户角色
            backUserCurrent.setRole(userDAO.getRoleUserByUserRole(getUserDO).getRole());
            return ResultUtil.success(timestamp, "用户查看的信息已准备完毕");
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }
}
