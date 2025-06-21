package com.orion.messagingService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.model.User;
import com.orion.messagingService.service.UserService;
import com.orion.messagingService.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-06-03 23:17:51
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




