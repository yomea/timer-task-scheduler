package com.xxx.task.utils;

import com.xxx.ace4j.Ace;
import com.xxx.ace4j.client.AaceClientConfig;
import com.xxx.ace4j.common.service.dto.ServerInfoDTO;
import com.xxx.ace4j.srd.ServiceNode;
import com.xxx.common.tools.result.ApiResult;
import com.xxx.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuzhenhong on 10/12/21 7:29 PM
 */
@Slf4j
public class AceServiceUtils {

    public static List<ServiceNode> getServerInfoList(String proxy, String interfaceName) {


        AaceClientConfig config = new AaceClientConfig();
        config.setProxy(proxy);
        config.setInterfaceName(interfaceName);

        //走缓存
        List<ServiceNode> serviceNodeList = Ace.get().serviceDiscovery().lookup(config.getAceUri());

//        ApiResult<List<ServerInfoDTO>> apiResult = Ace.get().aaceCenterClient().getProxy(proxy, interfaceName);
        if (CollectionUtils.isEmpty(serviceNodeList)) {
            log.error("getProxy error, result is empty. proxy:{}, interface:{}", proxy, interfaceName);
            return Collections.emptyList();
        }

        log.debug("注册到注册中心的机器：{}", GsonUtil.toJson(serviceNodeList));

        return serviceNodeList;
    }
}
