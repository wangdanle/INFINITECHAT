package com.orion.messagingService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.messagingService.mapper.UserBalanceMapper;
import com.orion.messagingService.model.UserBalance;
import com.orion.messagingService.service.UserBalanceService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_balance(用户余额表)】的数据库操作Service实现
* @createDate 2025-06-05 16:59:09
*/
@Service
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceMapper, UserBalance>
    implements UserBalanceService {

}




