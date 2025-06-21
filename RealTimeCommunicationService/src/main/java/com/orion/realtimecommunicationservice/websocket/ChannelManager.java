package com.orion.realtimecommunicationservice.websocket;


import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Orion
 * @CreateTime: 2025/5/29
 * @Description:
 */
public class ChannelManager {
    private final static ConcurrentHashMap<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<String, Channel>();
    private final static ConcurrentHashMap<Channel, String> CHANNEL_USER_MAP = new ConcurrentHashMap<Channel, String>();

    public static void addUserChannel(String userUuid, Channel channel){
        USER_CHANNEL_MAP.put(userUuid, channel);
    }

    public static void removeUserChannel(String userUuid){
        USER_CHANNEL_MAP.remove(userUuid);
    }

    public static Channel getChannelByUserId(String userUuid){
        return USER_CHANNEL_MAP.get(userUuid);
    }

    public static void addChannelUser(String userUuid, Channel channel){
        CHANNEL_USER_MAP.put(channel, userUuid);
    }

    public static void removeChannelUser(Channel channel){
        CHANNEL_USER_MAP.remove(channel);
    }

    public static String getUserIdByChannel(Channel channel){
        return CHANNEL_USER_MAP.get(channel);
    }
}
