package com.frontleaves.greenchaincarbonledger.utils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

/**
 * ResultUtil
 * <hr/>
 * 用于返回结果的工具类
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @see org.springframework.http.ResponseEntity
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
public class ResultUtil {
    /**
     * Success
     * <hr/>
     * 操作成功 - 不带数据
     *
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> success(Long timestamp) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.info("<200>Success | {} - 不带数据", "操作成功");
        // 返回结果
        return ResponseEntity
                .status(200)
                .body(new BaseResponse("Success", 200, "操作成功", null));
    }

    /**
     * Success
     * <hr/>
     * 操作成功 - 不带数据
     *
     * @param timestamp 时间戳
     * @param message   消息
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> success(Long timestamp, String message) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.info("<200>Success | {} - 不带数据", message);
        // 返回结果
        return ResponseEntity
                .status(200)
                .body(new BaseResponse("Success", 200, message, null));
    }

    /**
     * Success
     * <hr/>
     * 操作成功 - 带数据
     *
     * @param timestamp 时间戳
     * @param data      数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> success(Long timestamp, Object data) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.info("<200>Success | {} - 带数据", "操作成功");
        // 返回结果
        return ResponseEntity
                .status(200)
                .body(new BaseResponse("Success", 200, "操作成功", data));
    }

    /**
     * Success
     * <hr/>
     * 操作成功 - 带数据
     *
     * @param timestamp 时间戳
     * @param message   消息
     * @param data      数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> success(Long timestamp, String message, Object data) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.info("<200>Success | {} - 带数据", message);
        // 返回结果
        return ResponseEntity
                .status(200)
                .body(new BaseResponse("Success", 200, message, data));
    }

    /**
     * Error
     * <hr/>
     * 操作失败 - 不带数据
     *
     * @param timestamp 时间戳
     * @param errorCode 错误码
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> error(Long timestamp, ErrorCode errorCode) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.info("<{}>{} | {} - 不带数据", errorCode.code, errorCode.output, errorCode.message);
        // 返回结果
        return ResponseEntity
                .status(errorCode.code / 100)
                .body(new BaseResponse(errorCode.output, errorCode.code, errorCode.message, null));
    }

    /**
     * Error
     * <hr/>
     * 操作失败 - 带数据
     *
     * @param timestamp 时间戳
     * @param errorCode 错误码
     * @param data      数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> error(Long timestamp, ErrorCode errorCode, Object data) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.info("<{}>{} | {} - 带数据", errorCode.code, errorCode.output, errorCode.message);
        // 返回结果
        return ResponseEntity
                .status(errorCode.code / 100)
                .body(new BaseResponse(errorCode.output, errorCode.code, errorCode.message, data));
    }

    /**
     * Error
     * <hr/>
     * 操作失败 - 不带数据
     *
     * @param timestamp    时间戳
     * @param errorMessage 错误消息
     * @param errorCode    错误码
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> error(Long timestamp, String errorMessage, ErrorCode errorCode) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.warn("<{}>{}[{}] | {} - 不带数据", errorCode.code / 100, errorCode.output, errorCode.code, errorMessage);
        HashMap<String, String> errorData = new HashMap<>();
        errorData.put("errorMessage", errorMessage);
        // 返回结果
        return ResponseEntity
                .status(errorCode.code / 100)
                .body(new BaseResponse(errorCode.output, errorCode.code, errorCode.message, errorData));
    }

    /**
     * Error
     * <hr/>
     * 操作失败 - 带数据
     *
     * @param timestamp 时间戳
     * @param code      错误码
     * @param message   错误消息
     * @param output    输出
     * @param data      数据
     * @return BaseResponse
     * @since v1.0.0-SNAPSHOT
     */
    @NotNull
    public static ResponseEntity<BaseResponse> custom(Long timestamp, String output, Integer code, String message, Object data) {
        log.info("[Overhead] 开销时间 {} 毫秒", System.currentTimeMillis() - timestamp);
        log.info("<{}>{} | [自定义]{}", code, output, message);
        // 检查 code 位数
        int responseCode;
        if (code.toString().length() == 5) {
            responseCode = code / 100;
        } else {
            responseCode = code;
        }
        // 返回结果
        return ResponseEntity
                .status(responseCode)
                .body(new BaseResponse(output, code, message, data));
    }
}
