package com.orion.authenticationservice.controller;


import com.orion.authenticationservice.common.Result;
import com.orion.authenticationservice.data.user.login.LoginByCodeRequest;
import com.orion.authenticationservice.data.user.login.LoginByCodeResponse;
import com.orion.authenticationservice.data.user.login.LoginRequest;
import com.orion.authenticationservice.data.user.login.LoginResponse;
import com.orion.authenticationservice.data.user.register.RegisterRequest;
import com.orion.authenticationservice.data.user.register.RegisterResponse;
import com.orion.authenticationservice.data.user.update.UpdateAvatarRequest;
import com.orion.authenticationservice.data.user.update.UpdateAvatarResponse;
import com.orion.authenticationservice.service.UserService;
import com.orion.authenticationservice.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/19
 * @Description:用户类路由
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<RegisterResponse> register(@Validated @RequestBody RegisterRequest request) {
        RegisterResponse register = userService.register(request);

        return Result.OK(register);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        LoginResponse logined = userService.login(request);

        return Result.OK(logined);
    }

    @PostMapping("/loginbycode")
    public Result<LoginByCodeResponse> login(@Validated @RequestBody LoginByCodeRequest request) {
        LoginByCodeResponse logined = userService.loginByCode(request);

        return Result.OK(logined);
    }

    @PatchMapping("/avatar")
    public Result<UpdateAvatarResponse> updateAvatar(@Validated @RequestBody UpdateAvatarRequest request,
                                                     @RequestHeader String authorization){
        String id = JwtUtils.parseToken(authorization).getSubject();
        UpdateAvatarResponse response = userService.updateAvatar(id, request);

        return Result.OK(response);
    }


}
