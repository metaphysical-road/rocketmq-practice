package com.rocketmq.cloud.youxia.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.rocketmq.cloud.youxia.mapper")
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        //1.新建一个拦截器对象MybatisPlusInterceptor
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //2.添加子拦截器OptimisticLockerInnerInterceptor
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        //3.返回给Spring Framework的IOC容器
        return interceptor;
    }
}
