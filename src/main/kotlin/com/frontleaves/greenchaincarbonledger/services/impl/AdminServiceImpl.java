package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AdminUserChangeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackUserVO;
import com.frontleaves.greenchaincarbonledger.services.AdminService;
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


/**
 * AdminServiceImpl
 * <hr/>
 * 用于管理员服务的实现类，用于管理员相关的操作，提供管理员服务
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.AdminService
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserDAO userDAO;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> resetUserPassword(long timestamp, @NotNull HttpServletRequest request, @NotNull AdminUserChangeVO adminUserChangeVO) {
        log.info("[Service] 执行 resetUserPassword 方法");
        // 重置用户密码
        UserDO getUserDO = userDAO.getUserByUuid(adminUserChangeVO.getUuid());
        if (getUserDO != null) {
            log.debug("\t> 用户信息: {}", getUserDO.userName);
            // 检查修改UUID是否为自己
            if (getUserDO.getUuid().equals(ProcessingUtil.getAuthorizeUserUuid(request))) {
                return ResultUtil.error(timestamp, ErrorCode.CAN_T_RESET_MY_PASSWORD);
            }
            // 重置用户密码
            String newPassword = ProcessingUtil.createRandomString(10);
            getUserDO.setPassword(ProcessingUtil.passwordEncrypt(newPassword));
            if (userDAO.updateUserPassword(getUserDO)) {
                BackUserVO backUserVO = new BackUserVO();
                backUserVO
                        .setUuid(getUserDO.getUuid())
                        .setUserName(getUserDO.getUserName())
                        .setNickName(getUserDO.getNickName())
                        .setRealName(getUserDO.getRealName())
                        .setEmail(getUserDO.getEmail())
                        .setPhone(getUserDO.getPhone())
                        .setNewPassword(newPassword)
                        .setCreatedAt(getUserDO.getCreatedAt())
                        .setUpdatedAt(getUserDO.getUpdatedAt());
                return ResultUtil.success(timestamp, "重置用户密码成功", backUserVO);
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }
}
