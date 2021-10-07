package com.shinemo.task;

import com.shinemo.Aace.orginfo.model.OrgInfo;
import com.shinemo.common.tools.redis.RedisLock;
import com.shinemo.task.web.Application;
import com.shinemo.util.GsonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhaoyn
 * @Date 2019/11/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, properties = {"spring.profiles.active=daily"})
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedisLock redisLock;

    private ExecutorService pool = Executors.newFixedThreadPool(20);

    @Test
    public void testRedis() {
        String key = "flowable-ubpm-zyntest";
        stringRedisTemplate.opsForValue().set(key, "test");
        String value = stringRedisTemplate.opsForValue().get(key);
        System.out.println(value);
    }


    @Test
    public void testRedisTemplate() {
        String key = "hello";
        OrgInfo orgInfo = new OrgInfo();
        orgInfo.setAddress("杭州市西湖区");
        orgInfo.setCustomName("张三");
        redisTemplate.opsForValue().set(key, orgInfo, 1, TimeUnit.MINUTES);
        OrgInfo value = (OrgInfo) redisTemplate.opsForValue().get(key);
        System.out.println(GsonUtil.toJson(value));
    }

    @Test
    public void testRedisLock() {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                boolean lock = redisLock.tryLock("zyn_test_hhh", 5);
                return lock;
            }, pool);
            futures.add(future);
        }
        futures.forEach(v -> {
            boolean result = v.join();
            System.out.println(System.currentTimeMillis() + "----------" + result);
        });
    }


}

