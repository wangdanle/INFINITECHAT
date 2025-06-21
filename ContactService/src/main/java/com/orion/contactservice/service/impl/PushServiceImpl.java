package com.orion.contactservice.service.impl;

import com.alibaba.fastjson.JSON;

import com.orion.contactservice.constants.ConfigEnum;
import com.orion.contactservice.constants.UrlEnum;
import com.orion.contactservice.model.push.FriendApplicationNotification;
import com.orion.contactservice.model.push.NewGroupSessionNotification;
import com.orion.contactservice.model.push.NewSessionNotification;
import com.orion.contactservice.service.PushService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushServiceImpl implements PushService {

    private final OkHttpClient client;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public PushServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.client = new OkHttpClient();
    }

    @Override
    public void pushNewSession(Long userId, NewSessionNotification notification) throws Exception {
        String urlEndpoint = UrlEnum.PUSH_NEW_SESSION.getUrl();
        pushNotification(userId, notification, urlEndpoint, "用户已下线，推送创建新会话消息失败");
    }

    @Override
    public void pushNewApply(Long userId, FriendApplicationNotification notification) throws Exception {
        String urlEndpoint = UrlEnum.PUSH_NEW_APPLY.getUrl();
        pushNotification(userId, notification, urlEndpoint, "用户已下线，推送好友申请消息失败");
    }

    @Override
    public void pushGroupNewSession(Long userId, NewGroupSessionNotification notification) throws Exception {
        String urlEndpoint = UrlEnum.PUSH_NEW_GROUP_SESSION.getUrl();
        pushNotification(userId, notification, urlEndpoint, "用户已下线，推送创建群新会话消息失败");
    }

    /**
     * 通用的推送通知方法
     *
     * @param userId        用户ID
     * @param notification  通知对象
     * @param urlEndpoint   URL端点
     * @param offlineLogMsg 用户离线时的日志消息
     * @throws Exception
     */
    private void pushNotification(Long userId, Object notification, String urlEndpoint, String offlineLogMsg) throws Exception {
        String nettyUriKey = "user:session:" + userId;
        String urlIp = redisTemplate.opsForValue().get(nettyUriKey);

        if (urlIp != null) {
            String json = JSON.toJSONString(notification);
            MediaType mediaType = MediaType.get(ConfigEnum.MEDIA_TYPE.getValue());
            RequestBody requestBody = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url("http://" + urlIp + ":10010" + urlEndpoint + userId)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("推送消息失败，用户ID: {}, 响应码: {}, 响应消息: {}", userId, response.code(), response.message());
                    // 根据需求，可以选择是否抛出异常或执行其他逻辑
                } else {
                    log.info("成功推送消息给用户ID: {}", userId);
                }
            }
        } else {
            // 用户已下线，处理逻辑
            log.info(offlineLogMsg);
            // 根据需求，可以选择是否抛出异常或执行其他逻辑
            // throw new ServiceException(offlineLogMsg);
        }
    }
}
