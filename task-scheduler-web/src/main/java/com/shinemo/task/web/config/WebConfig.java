package com.shinemo.task.web.config;

import com.shinemo.task.web.filter.AccessLogFilter;
import com.shinemo.task.web.interceptor.ApiInterceptor;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhaoyn
 * @Date 2019/11/25
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private ApiInterceptor apiInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiInterceptor)
                .addPathPatterns("/**");
    }
    @Bean
    public FilterRegistrationBean logFilterRegiste() {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new AccessLogFilter());
        Set<String> set = new HashSet<>();
        set.add("/*");
        filterRegistrationBean.setUrlPatterns(set);
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}
