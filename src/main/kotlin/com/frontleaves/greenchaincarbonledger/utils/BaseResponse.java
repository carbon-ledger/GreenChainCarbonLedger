package com.frontleaves.greenchaincarbonledger.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * BaseResponse
 * <hr/>
 * 自定义返回结果
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
public record BaseResponse(String output, Integer code, String message, Object data) {

    public BaseResponse(String output, Integer code, String message, Object data) {
        this.output = output;
        this.code = code;
        this.message = message;
        this.data = data;
        log.info("============================================================");
    }

}
