package com.orion.momentservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.orion.momentservice.mapper")
public class MomentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentServiceApplication.class, args);
    }

}
