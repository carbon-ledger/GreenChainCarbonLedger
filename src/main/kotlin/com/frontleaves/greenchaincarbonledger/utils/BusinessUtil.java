package com.frontleaves.greenchaincarbonledger.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * BusinessUtil
 * <hr/>
 * 业务工具类, 用于处理业务逻辑中的一些公共方法
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng AND
 */
@Component
public class BusinessUtil {

    /**
     * 检查参数是否合法
     * <hr/>
     * 检查参数是否合法, 若不合法则返回错误信息, 若合法则返回 null, 用于检查 limit, page, order 参数
     *
     * @param timestamp 时间戳
     * @param limit     limit 参数
     * @param page      page 参数
     * @param order     order 参数
     * @return 若匹配则返回 null, 若不匹配则返回错误信息
     */
    public ResponseEntity<BaseResponse> checkLimitPageAndOrder(long timestamp, String limit, String page, String order) {
        if (limit != null && !limit.matches("^([0-9]+|)$")) {
            return ResultUtil.error(timestamp, "limit 参数错误", ErrorCode.PARAM_VARIABLE_ERROR);
        }
        if (page != null && !page.matches("^([0-9]+|)$")) {
            return ResultUtil.error(timestamp, "page 参数错误", ErrorCode.PARAM_VARIABLE_ERROR);
        }
        ArrayList<String> list = new ArrayList<>();
        list.add("desc");
        list.add("asc");
        if (order != null && !order.isEmpty()) {
            if (!list.contains(order)) {
                return ResultUtil.error(timestamp, "order 参数错误", ErrorCode.PARAM_VARIABLE_ERROR);
            }
        }
        return null;
    }
}
