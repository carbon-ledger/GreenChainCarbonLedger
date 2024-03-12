package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.PermissionDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.PermissionDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackPermissionVO;
import com.frontleaves.greenchaincarbonledger.services.PermissionService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    public ResponseEntity<BaseResponse> getPermissionList(long timestamp, @Nullable Integer limit, @Nullable Integer page, String order) {
        log.info("[Service] 执行 getPermissionList");
        // 检查参数，如果未设置（即为null），则使用默认值
        limit = (limit == null || limit > 100) ? 20 : limit;
        page = (page == null) ? 1 : page;
        if (order == null || order.isBlank()) {
            order = "asc";
        }
        log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
        //进行权限列表搜索
        order ="pid " + order;
        List<PermissionDO> getPermissionList;
        getPermissionList = permissionDAO.getPermissionListByAll(limit,page,order);
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
