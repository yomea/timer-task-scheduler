package com.shinemo.task.web.exception.handler;

import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.exception.ErrorCode;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.common.tools.result.Result;
import com.shinemo.task.core.error.BizErrors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ApiResult<?> handleApiException(HttpServletRequest request, ApiException ex) {
        log.error("handleApiException uri:{},ex: ", request.getRequestURI(), ex);
        return ApiResult.fail(ex.getMessage(), ex.getCode());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ApiResult<?> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        log.error("handleIllegalArgumentException uri:{},ex: ", request.getRequestURI(), ex);
        return ApiResult.fail(ex.getMessage(), BizErrors.PARAMTER_ERROR.code);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ApiResult<?> handleException(HttpServletRequest request, Throwable ex) {
        log.error("handleException uri:{},ex: ", request.getRequestURI(), ex);
        return ApiResult.fail("抱歉，系统有点小问题，请稍后再试", BizErrors.SERVICE_ERROR.code);
    }

}
