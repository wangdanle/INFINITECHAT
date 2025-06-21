package com.orion.contactservice.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.contactservice.mapper.UserMapper;
import com.orion.contactservice.model.User;
import com.orion.contactservice.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Zzw
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-10-08 16:08:49
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}




