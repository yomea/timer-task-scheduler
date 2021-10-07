package com.shinemo.task.core.config;

import com.alicp.jetcache.anno.support.MyKeyConvertorParser;
import com.alicp.jetcache.anno.support.SpringConfigProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JetcacheConfig {

    @Bean
    public SpringConfigProvider springConfigProvider() {
        SpringConfigProvider springConfigProvider = new SpringConfigProvider();
        springConfigProvider.setKeyConvertorParser(new MyKeyConvertorParser());
        return springConfigProvider;
    }
}
