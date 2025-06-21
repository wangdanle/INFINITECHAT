package com.orion.contactservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.contactservice.constants.FriendServiceConstants;
import com.orion.contactservice.data.searchUser.SearchUserRequest;
import com.orion.contactservice.data.searchUser.SearchUserResponse;
import com.orion.contactservice.exception.ServiceException;
import com.orion.contactservice.mapper.FriendMapper;
import com.orion.contactservice.mapper.UserSessionMapper;
import com.orion.contactservice.model.Friend;
import com.orion.contactservice.model.User;
import com.orion.contactservice.service.FriendService;
import com.orion.contactservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {
    private final UserService userService;

    private final UserSessionMapper userSessionMapper;


    /**
     * @param request
     * @return
     */
    @Override
    public SearchUserResponse searchUser(SearchUserRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("phone", request.getPhone());
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            throw new ServiceException(FriendServiceConstants.USER_NOT_EXIST);
        }

        validateFriendUser(user);

        SearchUserResponse response = new SearchUserResponse();
        response.setUserUuid(user.getUserId().toString());
        response.setNickName(user.getUserName());
        response.setAvatar(user.getAvatar());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setSignature(user.getSignature());
        response.setGender(user.getGender());
        response.setStatus(user.getStatus());

        populateSessionid(Long.getLong(request.getUserUuid()), user.getUserId(), response);
        populateFriendStatus(Long.getLong(request.getUserUuid()), user.getUserId(), response);

        return response;
    }

    private SearchUserResponse getFriendDetails(Long userUuid, Long friendUUId) {
        return null;
    }

    private void validateFriendUser(User friendUser) {
        switch (friendUser.getStatus()) {
            case FriendServiceConstants.USER_STATUS_BANNED:
                throw new ServiceException(FriendServiceConstants.USER_BANNED);
            case FriendServiceConstants.USER_STATUS_DELETED:
                throw new ServiceException(FriendServiceConstants.USER_DELETED);
            default:
                break;
        }
    }

    private void populateSessionid(Long userId, Long friendId, SearchUserResponse response) {
        List<Long> commonSessionIds = userSessionMapper.findCommonSingleChatSessionIds(userId, friendId);
        if (commonSessionIds == null || commonSessionIds.isEmpty()) {
            response.setSessionId(null);
        } else {
            response.setSessionId(String.valueOf(commonSessionIds.get(0)));
        }
    }

    private void populateFriendStatus(Long userId, Long friendId, SearchUserResponse response) {
        QueryWrapper<Friend> wrapper = new QueryWrapper<>();
        wrapper.eq("friend_id", friendId).eq("user_id", userId);
        Friend friend = this.getOne(wrapper);
        if (friend != null) {
            response.setStatus(friend.getStatus());
        } else {
            response.setStatus(FriendServiceConstants.FRIEND_STATUS_NON_FRIEND);
        }
    }



}
