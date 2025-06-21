package com.orion.authenticationservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.authenticationservice.data.user.login.LoginByCodeRequest;
import com.orion.authenticationservice.data.user.login.LoginByCodeResponse;
import com.orion.authenticationservice.data.user.login.LoginRequest;
import com.orion.authenticationservice.data.user.login.LoginResponse;
import com.orion.authenticationservice.data.user.register.RegisterRequest;
import com.orion.authenticationservice.data.user.register.RegisterResponse;
import com.orion.authenticationservice.data.user.update.UpdateAvatarRequest;
import com.orion.authenticationservice.data.user.update.UpdateAvatarResponse;
import com.orion.authenticationservice.model.User;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-05-20 16:56:15
*/
public interface UserService extends IService<User> {
    default User getOnly(QueryWrapper<User> queryWrapper, boolean throwEx) {
        queryWrapper.last("limit 1");
        return this.getOne(queryWrapper);
    }

    /**
     * 注册接口
     * @param request
     * @return
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * 通过密码登录接口
     * @param request
     * @return
     */
    LoginResponse login(LoginRequest request);

    /**
     * 通过验证码登录接口
     * @param request
     * @return
     */
    LoginByCodeResponse loginByCode(LoginByCodeRequest request);

    /**
     * 更新头像接口
     * @param request
     * @return
     */
    UpdateAvatarResponse updateAvatar(String id, UpdateAvatarRequest request);

}
