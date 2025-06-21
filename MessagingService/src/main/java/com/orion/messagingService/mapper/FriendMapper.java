package com.orion.messagingService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.messagingService.model.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【friend(联系人表)】的数据库操作Mapper
* @createDate 2025-06-03 23:17:51
* @Entity generator.domain.Friend
*/
@Mapper
public interface FriendMapper extends BaseMapper<Friend> {
    Friend selectFriendShip (@Param("userId") Long userId, @Param("friendId") Long friendId);

}




