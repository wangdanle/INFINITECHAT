package com.orion.authenticationservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.authenticationservice.exception.DataBaseException;
import com.orion.authenticationservice.mapper.UserBalanceMapper;
import com.orion.authenticationservice.model.UserBalance;
import com.orion.authenticationservice.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Administrator
 * @description 针对表【user_balance(用户余额表)】的数据库操作Service实现
 * @createDate 2025-06-05 16:59:09
 */
@Service
@RequiredArgsConstructor
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceMapper, UserBalance> implements UserBalanceService {

    private final UserBalanceMapper userBalanceMapper;

    @Transactional(rollbackFor = Exception.class)
    public void createUserBalance(Long userId) {
        UserBalance userBalance = new UserBalance()
                .setUserId(userId)
                .setBalance(BigDecimal.valueOf(1000))
                .setUpdatedAt(LocalDateTime.now());

        if (userBalanceMapper.insert(userBalance) != 1) {
            throw new DataBaseException("创建用户余额失败");
        }
    }
}




