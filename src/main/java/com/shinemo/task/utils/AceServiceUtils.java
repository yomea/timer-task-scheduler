package com.shinemo.task.utils;

import com.shinemo.ace4j.Ace;
import com.shinemo.ace4j.common.service.dto.ServerInfoDTO;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuzhenhong on 10/12/21 7:29 PM
 */
@Slf4j
public class AceServiceUtils {

    public static List<ServerInfoDTO> getServerInfoList(String proxy, String interfaceName) {

        ApiResult<List<ServerInfoDTO>> apiResult = Ace.get().aaceCenterClient().getProxy(proxy, interfaceName);
        if (!apiResult.isSuccess() || CollectionUtils.isEmpty(apiResult.getData())) {
            log.error("getProxy error, result is empty. proxy:{}, interface:{}", proxy, interfaceName);
            return Collections.emptyList();
        }

        log.info("注册到注册中心的机器：{}", GsonUtil.toJson(apiResult.getData()));

        return apiResult.getData();
    }
}
