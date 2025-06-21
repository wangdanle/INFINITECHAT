package com.orion.contactservice.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orion.contactservice.constants.*;
import com.orion.contactservice.data.createGroup.CreateGroupRequest;
import com.orion.contactservice.data.createGroup.CreateGroupResponse;
import com.orion.contactservice.exception.DataBaseException;
import com.orion.contactservice.exception.UserException;
import com.orion.contactservice.mapper.SessionMapper;
import com.orion.contactservice.mapper.UserMapper;
import com.orion.contactservice.mapper.UserSessionMapper;
import com.orion.contactservice.model.Session;
import com.orion.contactservice.model.User;
import com.orion.contactservice.model.UserSession;
import com.orion.contactservice.model.push.NewGroupSessionNotification;
import com.orion.contactservice.service.PushService;
import com.orion.contactservice.service.SessionService;
import com.orion.contactservice.service.UserSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【session(会话表)】的数据库操作Service实现
 * @createDate 2025-06-13 11:46:16
 */
@Service
@Slf4j
public class SessionServiceImpl extends ServiceImpl<SessionMapper, Session>
        implements SessionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private UserSessionMapper userSessionMapper;

    @Autowired
    private PushService pushService;

    @Autowired
    private UserSessionService userSessionService;


    /**
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateGroupResponse createGroup(CreateGroupRequest request) {
        Long creatorId = request.getCreatorId();
        List<Long> memberIds = request.getMemberIds();
        List<String> failedMemberIds = new ArrayList<>();

        // 确认创建者用户存在且状态正常
        User creator = getActiveUserById(creatorId);

        // 生成sessionId
        Long sessionId = generateId();

        // 生成群名称
        String groupName = generateGroupName(creatorId, memberIds);

        // 插入session表
        createSession(sessionId, groupName);

        // 插入user_session表-创建者
        insertUserSession(sessionId, creatorId);

        // 构建推送新群会话消息
        NewGroupSessionNotification notification = buildNewGroupSessionNotification(creatorId, sessionId, groupName);

        // 插入user_session表-其他成员并推送通知
        insertMembersAndPushNotifications(memberIds, sessionId, notification, failedMemberIds);

        // 响应结果
        CreateGroupResponse response = new CreateGroupResponse();
        BeanUtils.copyProperties(notification, response);
        response.setFailedMemberIds(failedMemberIds);
        return response;
    }

    /**
     * 插入成员和推送通知
     *
     * @param memberIds       成员id
     * @param sessionId       会话ID
     * @param notification    通知
     * @param failedMemberIds 失败的成员id
     */
    private void insertMembersAndPushNotifications(List<Long> memberIds, Long sessionId, NewGroupSessionNotification notification, List<String> failedMemberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }
        // 1.批量构建UserSession对象
        List<UserSession> userSessions = new ArrayList<>(memberIds.size());
        for (Long memberId : memberIds) {
            userSessions.add(new UserSession()
                    .setId(generateId())
                    .setUserId(memberId)
                    .setSessionId(sessionId)
                    .setRole(UserRole.GROUP_MEMBER.getValue())
                    .setStatus(UserStatus.NORMAL.getValue()));

            // 2.使用MyBatis Plus的批量插入功能
            try {
                // 使用MyBatis Plus的批量保存方法
                userSessionService.saveBatch(userSessions);
            } catch (Exception e) {
                log.error("批量插入用户会话关系失败,sessionId:{},错误:{}", sessionId, e.getMessage());
                throw new DataBaseException(ErrorEnum.DATABASE_ERR);
            }
            // 3.批量推送通知(保持原有的错误处理逻辑)
            for (Long memberid:memberIds){
                try{
                    pushService.pushGroupNewSession(memberid, notification);
                } catch (Exception e) {
                    failedMemberIds.add(String.valueOf(memberid));
                    log.error("推送群聊会话失败,成员ID{},错误信息:{}", memberId, e.getMessage());
                }
            }
        }
    }

    /**
     * 构建新的群聊通知
     *
     * @param creatorId 创建者id
     * @param sessionId 会话ID
     * @param groupName 组名
     * @return {@link NewGroupSessionNotification}
     */
    private NewGroupSessionNotification buildNewGroupSessionNotification(Long creatorId, Long sessionId, String groupName) {
        return new NewGroupSessionNotification()
                .setCreatorId(String.valueOf(creatorId))
                .setSessionId(String.valueOf(sessionId))
                .setSessionName(groupName)
                .setSessionType(SessionType.GROUP.getValue())
                .setAvatar(ConfigEnum.GROUP_AVATAR_URL.getValue());
    }

    /**
     * 插入用户会话
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     */
    private void insertUserSession(Long sessionId, Long userId) {
        UserSession userSession = new UserSession()
                .setId(generateId())
                .setUserId(userId)
                .setSessionId(sessionId)
                .setRole(UserRole.GROUP_OWNER.getValue())
                .setStatus(UserStatus.NORMAL.getValue());
        userSessionMapper.insert(userSession);
    }


    /**
     * 创建会话
     *
     * @param sessionId 会话ID
     * @param groupName 组名
     */
    private void createSession(Long sessionId, String groupName) {
        sessionMapper.insert(new Session()
                .setId(sessionId)
                .setName(groupName)
                .setType(SessionType.GROUP.getValue())
                .setStatus(SessionType.STATUS_NORMAL.getValue()));
    }

    private String generateGroupName(Long creatorId, List<Long> memberIds) {
        return "测试群聊名字:" + IdUtil.fastSimpleUUID().substring(0, 4);
    }

    private Long generateId() {
        return IdUtil.getSnowflake(1, 1).nextId();
    }

    /**
     * 通过ID获取状态活跃用户
     *
     * @param userId 用户ID
     * @return {@link User}
     */
    private User getActiveUserById(Long userId) {
        User user = userMapper.selectById(userId);
        // 判断用户是否为空和状态是否异常
        if (user == null) {
            throw new UserException(ErrorEnum.NO_USER_ERROR);
        }
        if (user.getStatus() != UserStatus.NORMAL.getValue())
            throw new UserException(ErrorEnum.USER_STATUS_ERROR);
        return user;
    }
}





