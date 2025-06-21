package com.orion.momentservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.momentservice.mapper.FriendMapper;
import com.orion.momentservice.model.Friend;
import com.orion.momentservice.service.FriendService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zzw
 * @description 针对表【friend(联系人表)】的数据库操作Service实现
 * @createDate 2024-10-08 15:09:48
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Override
    public List<Long> getFriendIds(Long userId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        List<Friend> friends = this.list(queryWrapper);
        List<Long> friendIds = friends.stream().map(Friend::getFriendId).collect(Collectors.toList());
        return friendIds;
    }

}




