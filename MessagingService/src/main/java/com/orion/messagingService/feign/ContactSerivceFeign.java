package com.orion.messagingService.feign;


import com.orion.messagingService.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: Orion
 * @CreateTime: 2025/6/3
 * @Description:
 */
@FeignClient("ContactSerivce")
public interface ContactSerivceFeign {
    @GetMapping("/api/v1/contact/user")
    Result<?> getUser();
}
