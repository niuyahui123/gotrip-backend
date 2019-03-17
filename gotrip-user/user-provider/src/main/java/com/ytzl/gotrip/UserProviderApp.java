package com.ytzl.gotrip;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.ytzl.gotrip.mapper.GotripUserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户模块提供者
 *
 * @author jayden
 */
@SpringBootApplication
@MapperScan(basePackageClasses = GotripUserMapper.class)
@EnableDubboConfiguration
public class UserProviderApp {

    //psvm
    public static void main(String[] args) {
        SpringApplication.run(UserProviderApp.class, args);
    }

}
