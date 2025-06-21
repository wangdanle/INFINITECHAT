package com.orion.realtimecommunicationservice.websocket;


import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.orion.realtimecommunicationservice.constant.MessageTypeEnum;
import com.orion.realtimecommunicationservice.constant.UserConstants;
import com.orion.realtimecommunicationservice.exception.MessageTypeException;
import com.orion.realtimecommunicationservice.model.AckData;
import com.orion.realtimecommunicationservice.model.LogOutData;
import com.orion.realtimecommunicationservice.model.MessageDTO;
import com.orion.realtimecommunicationservice.utils.JwtUtils;
import com.orion.realtimecommunicationservice.utils.NettyUtils;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.InetAddress;

@Slf4j
@Sharable
public class MessageInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public MessageInboundHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private StringRedisTemplate redisTemplate;

    // 连接建立时触发
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("客户端连接: " + ctx.channel().id().asShortText());
        System.out.println("处理器添加时间: " + System.currentTimeMillis());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("欢迎连接WebSocket"));
    }

    // 处理文本消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String request = msg.text();
        System.out.println("收到消息: " + request);
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器响应: " + request));

        MessageDTO messageDTO = JSONUtil.toBean(msg.text(), MessageDTO.class);

        MessageTypeEnum messageType = MessageTypeEnum.of(messageDTO.getType());
        switch (messageType) {
            case ACK:
                processACK(messageDTO);
            case LOG_OUT:
                proccessLogOut(ctx, messageDTO);
            case HEART_BEAT:
                precessHeartBeat(ctx, messageDTO);
            case ILLEGAL:
                processIllegal(messageDTO);
        }
    }

    private void processACK(MessageDTO msg) {
        AckData ackData = JSONUtil.toBean(msg.getData().toString(), AckData.class);
        log.info("ackData:{}", ackData);
        log.info("推送消息成功! ");
    }

    private void proccessLogOut(ChannelHandlerContext ctx, MessageDTO msg) {
        LogOutData logOutData = JSONUtil.toBean(msg.getData().toString(), LogOutData.class);
        Integer userUuid = logOutData.getUserUuid();
        log.info("请求断开用户{}的连接...", userUuid);
        offline(ctx);
        log.info("断开连接成功!");
    }

    private void precessHeartBeat(ChannelHandlerContext ctx, MessageDTO msg) {
        log.info("收到心跳包");
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(MessageTypeEnum.HEART_BEAT.getCode());
        TextWebSocketFrame frame = new TextWebSocketFrame(JSONUtil.toJsonStr(messageDTO));
        ctx.channel().writeAndFlush(frame);
    }

    private void processIllegal(MessageDTO msg) {
        throw new MessageTypeException("不支持的消息格式!");
    }

    /**
     * 通道活动
     *
     * @param ctx ctx
     * @throws Exception 无效
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接激活时间: " + System.currentTimeMillis());
        ctx.fireChannelActive();
    }

    /**
     * 通道不活动
     *
     * @param ctx ctx
     * @throws Exception 无效
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        offline(ctx);
        super.channelInactive(ctx);
    }

    // 连接关闭时触发
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("客户端断开: " + ctx.channel().id().asShortText());
    }

    // 异常处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("俘获到异常:", cause);
        try {
            offline(ctx);
        } catch (Exception e) {
            log.error("关闭管道失败:", e);
        } finally {
            ctx.close();
        }
    }

    /**
     * 心跳处理
     *
     * @param ctx
     * @param event
     */
    private void handleIdleStateEvent(ChannelHandlerContext ctx, IdleStateEvent event) {
        switch (event.state()) {
            case READER_IDLE:
                log.error("读空闲超时,关闭连接...{},用户ID:{}",
                        ctx.channel().remoteAddress(),
                        ChannelManager.getUserIdByChannel(ctx.channel()));
                offline(ctx);
                break;
            case WRITER_IDLE:
                log.warn("写空闲超时,连接:{}", ctx.channel().remoteAddress());  // 改用warn级别
                break;
            case ALL_IDLE:
                log.warn("读写空闲超时,连接:{}", ctx.channel().remoteAddress()); // 改用warn级别
                break;
        }
    }

    private void handleHandshakeComplete(ChannelHandlerContext ctx) throws Exception {
        String userUuid = NettyUtils.getAttr(ctx.channel(), NettyUtils.UID);
        String token = NettyUtils.getAttr(ctx.channel(), NettyUtils.TOKEN);

        if (!validateToken(userUuid, token)) {
            log.warn("Token无效,关闭连接: {}", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }

        // 先清除旧连接再设置新状态
        clearExistingConnection(userUuid);

        // 设置Redis状态
        String localIp = InetAddress.getLocalHost().getHostAddress();
        redisTemplate.opsForValue().set(
                UserConstants.USER_SESSION + userUuid,
                localIp
        );

        // 注册新连接
        registerNewConnection(ctx, userUuid);
        log.info("客户端认证成功,用户ID:{} 地址:{}", userUuid, ctx.channel().remoteAddress());
    }

    private void clearExistingConnection(String userUuid) {
        Channel existing = ChannelManager.getChannelByUserId(userUuid);
        if (existing != null) {
            log.info("关闭用户已有连接: {} {}", userUuid, existing.remoteAddress());
            ChannelManager.removeUserChannel(userUuid);
            ChannelManager.removeChannelUser(existing);
            try {
                existing.close().sync();
            } catch (InterruptedException e) {
                log.warn("关闭旧连接中断异常: {} {}", userUuid, existing.remoteAddress(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void registerNewConnection(ChannelHandlerContext ctx, String userUuid) {
        // 移除可能存在的残留映射
        ChannelManager.removeUserChannel(userUuid);
        ChannelManager.removeChannelUser(ctx.channel());

        // 添加新映射
        ChannelManager.addUserChannel(userUuid, ctx.channel());
        ChannelManager.addChannelUser(userUuid, ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳处理
        if (evt instanceof IdleStateEvent) {
            handleIdleStateEvent(ctx, (IdleStateEvent) evt);
        }
        // 握手完成处理,协议升级
        else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            handleHandshakeComplete(ctx);
        }
    }

    /**
     * 下线的接口
     *
     * @param ctx ctx
     */
    private void offline(ChannelHandlerContext ctx) {
        String userUuid = ChannelManager.getUserIdByChannel(ctx.channel());

        try {
            ChannelManager.removeChannelUser(ctx.channel());
            if (!StringUtils.isEmpty(userUuid)) {
                ChannelManager.removeUserChannel(userUuid);
                log.info("客户端关闭连接userId:{}, 客户端地址为:{}", userUuid, ctx.channel().remoteAddress());
            }
        } catch (Exception e) {
            log.error("处理退出登录异常", e);
        } finally {
            // 关闭通道
            if (ctx.channel() != null) {
                ctx.channel().close();
            }
            // 在Redis删除对应的key
            redisTemplate.opsForValue().getAndDelete(UserConstants.USER_SESSION + userUuid);
        }
    }

    /**
     * 校验身份
     *
     * @param userUuid
     * @param token
     * @return
     */
    private boolean validateToken(String userUuid, String token) {
        if (token != null) {
            token = token.replace("\n", "").replace("\r", "");
        }

        Claims claims = JwtUtils.parseToken(token);
        String userId = claims.getSubject();

        return userId != null && userId.equals(userUuid);
    }


}