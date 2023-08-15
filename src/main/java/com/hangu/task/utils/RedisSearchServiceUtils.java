package com.hangu.task.utils;

import cn.hutool.json.JSONUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * Created by wuzhenhong on 10/12/21 7:29 PM
 */
@Slf4j
public class RedisSearchServiceUtils {

    private static JedisSentinelPool JEDIS_SENTINEL_POOL;
    public RedisSearchServiceUtils(JedisSentinelPool jedisSentinelPool) {
        RedisSearchServiceUtils.JEDIS_SENTINEL_POOL = jedisSentinelPool;
    }

    public static List<String> getServerInfoList(String appName) {

        Jedis jedis = JEDIS_SENTINEL_POOL.getResource();
        Map<String, String> map;
        try {
            map =  jedis.hgetAll(appName);
        } finally {
            jedis.close();
        }
        if(CollectionUtils.isEmpty(map)) {
            log.error("getProxy error, result is empty. appname:{}", appName);
            return Collections.emptyList();
        }

        long current = System.currentTimeMillis();
        List<String> ipList = map.entrySet().stream().filter(entry -> {
            Long expire = Long.parseLong(entry.getValue());
            return expire > current;
        }).map(Entry::getKey).distinct().collect(Collectors.toList());

        log.debug("注册到注册中心的机器：{}", JSONUtil.toJsonStr(ipList));
        return ipList;
    }
}
