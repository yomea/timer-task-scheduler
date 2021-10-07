package com.shinemo.task.core.config;

import com.shinemo.Aace.imlogin.client.IMLoginClient;
import com.shinemo.Aace.userprofice.client.UserProficeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaoyn
 * @Date 2019/12/18
 */
@Configuration
public class ConsumerConfig {

    @Value("${aace.center.uri}")
    private String center;


    /**
     * 用不到的记得删除，减少连接数
     */
    @Bean
    public UserProficeClient userProficeClient() {
        UserProficeClient userProficeClient = new UserProficeClient();
        userProficeClient.setProxy("UserProfice");
        userProficeClient.setUri(center);
        userProficeClient.init();
        return userProficeClient;
    }

    /**
     * 用不到的记得删除，减少连接数
     */
    @Bean
    public IMLoginClient imLoginClient() {
        IMLoginClient smsClient = new IMLoginClient();
        smsClient.setProxy("IMLogin");
        smsClient.setUri(center);
        smsClient.init();
        return smsClient;
    }

}
