package com.orion.authenticationservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.orion.authenticationservice.model.UserBalance;

/**
* @author Administrator
* @description 针对表【user_balance(用户余额表)】的数据库操作Service
* @createDate 2025-06-05 16:59:09
*/
public interface UserBalanceService extends IService<UserBalance> {
    void createUserBalance(Long userId);
}
