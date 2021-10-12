package com.shinemo.task.callback;

import com.shinemo.Aace.RetCode;
import com.shinemo.ace4j.protocol.AceCallback;
import com.shinemo.ace4j.protocol.codec.AceResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wuzhenhong on 10/12/21 11:44 AM
 */
public interface TaskSchedulerCallback extends AceCallback {

    @Override
    default void onResponse(AceResponse response) {

        int retCode = response.getStatusCode();
        if(RetCode.RET_SUCCESS == retCode) {
            success(retCode);
        } else if(RetCode.RET_TIMEOUT == retCode) {
            timeout(retCode);
        } else if(RetCode.RET_FAILURE == retCode) {
            Throwable e = response.getCause();
            exception(retCode, e);
        } else {
            failure(retCode);
        }
    }

    void failure(int retCode);

    void exception(int retCode, Throwable e);

    void success(int retCode);

    void timeout(int retCode);
}
