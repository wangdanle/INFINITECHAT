package com.orion.authenticationservice.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.authenticationservice.constant.user.RegisterConstant;
import com.orion.authenticationservice.constant.user.UserErrorEnum;
import com.orion.authenticationservice.data.user.login.LoginByCodeRequest;
import com.orion.authenticationservice.data.user.login.LoginByCodeResponse;
import com.orion.authenticationservice.data.user.login.LoginRequest;
import com.orion.authenticationservice.data.user.login.LoginResponse;
import com.orion.authenticationservice.data.user.register.RegisterRequest;
import com.orion.authenticationservice.data.user.register.RegisterResponse;
import com.orion.authenticationservice.data.user.update.UpdateAvatarRequest;
import com.orion.authenticationservice.data.user.update.UpdateAvatarResponse;
import com.orion.authenticationservice.exception.DataBaseException;
import com.orion.authenticationservice.exception.UserException;
import com.orion.authenticationservice.mapper.UserBalanceMapper;
import com.orion.authenticationservice.mapper.UserMapper;
import com.orion.authenticationservice.model.User;
import com.orion.authenticationservice.model.UserBalance;
import com.orion.authenticationservice.service.UserBalanceService;
import com.orion.authenticationservice.service.UserService;
import com.orion.authenticationservice.utils.JwtUtils;
import com.orion.authenticationservice.utils.NickNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author Administrator
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-05-20 16:56:15
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final StringRedisTemplate redisTemplate;

    private final UserBalanceService userBalanceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterRequest request) {
        // 1. 参数校验
        validateRegisterRequest(request);

        // 2.确定手机号有没有注册
        String phone = request.getPhone();
        if (accountIsExist(phone)) {
            throw new UserException(UserErrorEnum.REGISTER_ERR);
        }

        // 3.检验验证码
        validateVerificationCode(phone, request.getCode());

        // 4.创建用户实体
        User user = createUser(request);

        // 5.保存用户
        boolean isSaveUser = this.save(user);
        if (!isSaveUser) {
            throw new DataBaseException("数据库异常,保存用户数据信息失败");
        }

        // 6. 保存余额
        userBalanceService.createUserBalance(user.getUserId());

        return new RegisterResponse().setPhone(phone);
    }

    /**
     * 创建用户
     *
     * @param request 要求
     * @return {@link User}
     */
    private User createUser(RegisterRequest request) {
        String encryptPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes(StandardCharsets.UTF_8));
        return new User()
                .setUserId(IdUtil.getSnowflake(1, 1).nextId())
                .setPhone(request.getPhone())
                .setPassword(encryptPassword)
                .setUserName(NickNameGenerator.generate());
    }

    /**
     * 检验验证码
     * @param phone 手机号
     * @param inputCode 输入的验证码
     */
    private void validateVerificationCode(String phone, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get(RegisterConstant.REGISTER_CODE + phone);
        if (!StringUtils.hasLength(storedCode) || !storedCode.equals(inputCode)) {
            throw new UserException(UserErrorEnum.CODE_ERR);
        }
    }

    /**
     * 验证参数
     *
     * @param request 要求
     */
    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null || !StringUtils.hasText(request.getPhone()) || !StringUtils.hasText(request.getPassword()) || !StringUtils.hasText(request.getCode())) {
            throw new IllegalArgumentException("注册参数不合法");
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String phone = request.getPhone();
        String password = request.getPassword();

        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("phone", phone);
        User one = this.getOnly(userWrapper, true);

        if (one == null) {
            throw new UserException(UserErrorEnum.USER_ERR);
        }
        if (!StringUtils.hasLength(password) || !DigestUtils.md5DigestAsHex(password.getBytes()).equals(one.getPassword())) {
            throw new UserException(UserErrorEnum.USER_ERR, "登录账户或者密码错误");
        }

        LoginResponse loginResponse = new LoginResponse();
        BeanUtils.copyProperties(one, loginResponse);

        String token = JwtUtils.generate(one.getUserId().toString());
        loginResponse.setToken(token);

        return loginResponse;
    }

    @Override
    public LoginByCodeResponse loginByCode(LoginByCodeRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();

        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("phone", phone);
        User one = this.getOnly(userWrapper, true);

        if (one == null) {
            throw new UserException(UserErrorEnum.USER_ERR);
        }

        String s = redisTemplate.opsForValue().get(RegisterConstant.REGISTER_CODE + phone);
        if (!StringUtils.hasLength(code) || !code.equals(s)) {
            throw new UserException(UserErrorEnum.CODE_ERR);
        }

        // 设置返回信息
        LoginByCodeResponse loginByCodeResponse = new LoginByCodeResponse();
        BeanUtils.copyProperties(one, loginByCodeResponse);
        String token = JwtUtils.generate(one.getUserId().toString());
        loginByCodeResponse.setToken(token);

        return loginByCodeResponse;
    }

    @Override
    public UpdateAvatarResponse updateAvatar(String id, UpdateAvatarRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", Long.valueOf(id));

        User user = this.getOnly(queryWrapper, true);
        if (user == null) {
            throw new UserException(UserErrorEnum.USER_ERR);
        }

        String avatarUrl = request.getAvatarUrl();
        user.setAvatar(avatarUrl);
        boolean isUpdate = this.updateById(user);
        if (!isUpdate) {
            throw new DataBaseException(UserErrorEnum.UPTADE_AVATAR_ERR);
        }

        UpdateAvatarResponse response = new UpdateAvatarResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    /**
     * 账户已经注册过了
     *
     * @param phone
     * @return
     */
    private boolean accountIsExist(String phone) {
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("phone", phone);
        long count = this.count(userWrapper);
        return count > 0;
    }
}




