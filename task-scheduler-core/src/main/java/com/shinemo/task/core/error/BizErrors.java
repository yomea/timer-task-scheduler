package com.shinemo.task.core.error;

import com.shinemo.common.tools.exception.ErrorCode;

/**
 * @author zhaoyn
 * @Date 2019/3/19
 */
public interface BizErrors {

    ErrorCode SERVICE_ERROR = new ErrorCode(500, "服务错误");
    ErrorCode PARAMTER_ERROR = new ErrorCode(1001, "参数错误");
}
