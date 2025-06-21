package com.orion.contactservice.service;


import com.orion.contactservice.model.push.FriendApplicationNotification;
import com.orion.contactservice.model.push.NewGroupSessionNotification;
import com.orion.contactservice.model.push.NewSessionNotification;

public interface PushService {
    /**
     * 推送新会话信息
     *
     * @param userId
     * @param notification
     * @throws Exception
     */
    void pushNewSession(Long userId, NewSessionNotification notification) throws Exception;

    /**
     * 推送好友申请
     *
     * @param userId
     * @param notification
     * @throws Exception
     */
    public void pushNewApply(Long userId, FriendApplicationNotification notification) throws Exception;


    /**
     * 推送新群会话消息
     *
     * @param userId
     * @param notification
     * @throws Exception
     */
    public void pushGroupNewSession(Long userId, NewGroupSessionNotification notification) throws Exception;


}