package com.orion.authenticationservice.conf;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/28
 * @Description:
 */
@Component
public class Interceptors implements WebMvcConfigurer {
    @Autowired
    private SourceHandler sourceHandler;

    @Autowired
    private JwtHandler jwtHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sourceHandler)
                .addPathPatterns("/**");
        registry.addInterceptor(jwtHandler)
                .addPathPatterns(new ArrayList<>(Arrays.asList("/api/v1/user/avatar")));
    }
}
