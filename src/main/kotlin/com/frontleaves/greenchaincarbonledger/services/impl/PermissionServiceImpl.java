package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.PermissionDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.PermissionDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackPermissionVO;
import com.frontleaves.greenchaincarbonledger.services.PermissionService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FLASHLACK
 * @since 2024-03-08
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionDAO permissionDAO;
    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getPermissionList(long timestamp, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 getPermissionList 方法");
        List<PermissionDO> getPermissionList;
        getPermissionList = permissionDAO.getAllPermissionList();
        //整理数据
        ArrayList<BackPermissionVO> backPermissionList = new ArrayList<>();
        for (PermissionDO getPermission : getPermissionList ){
            BackPermissionVO backPermissionVO = new BackPermissionVO();
            backPermissionVO.setName(getPermission.getName());
            backPermissionVO.setDescription(getPermission.getDescription());
            backPermissionList.add(backPermissionVO);
        }
        return ResultUtil.success(timestamp,"权限列表信息已准备完毕",backPermissionList);
    }
}
