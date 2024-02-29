package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.AdminUserChangeVO;
import com.frontleaves.greenchaincarbonledger.services.AdminService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


/**
 * AdminServiceImpl
 * <hr/>
 * 用于管理员服务的实现类，用于管理员相关的操作，提供管理员服务
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.AdminService
 * @author xiao_lfeng
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserDAO userDAO;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> resetUserPassword(long timestamp, @NotNull HttpServletRequest request, @NotNull AdminUserChangeVO adminUserChangeVO) {
        // 重置用户密码
        UserDO getUserDO = userDAO.getUserByUuid(adminUserChangeVO.getUuid());
        if (getUserDO != null) {
            // 重置用户密码
            String newPassword = ProcessingUtil.createRandomNumbers(10);
            String passwordEncode = ProcessingUtil.passwordEncrypt(newPassword);
            getUserDO.setPassword(passwordEncode);
            if (userDAO.updateUserPassword(getUserDO)) {
                return ResultUtil.success(timestamp, "重置用户密码成功，新密码为：" + newPassword);
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }
}
