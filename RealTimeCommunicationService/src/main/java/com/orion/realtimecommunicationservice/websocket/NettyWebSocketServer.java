package com.orion.realtimecommunicationservice.websocket;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class NettyWebSocketServer {
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());
    private Channel channel;

    @Value("${netty.port}")
    private int port;

    @Value("${netty.name}")
    private String serverName;

    @Autowired
    private NacosServiceManager nacosServiceManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final DiscoveryClient discoveryClient;

    @PostConstruct // Spring Bean初始化后执行
    public void start() throws InterruptedException, UnknownHostException, NacosException {
        run();
        NamingService namingService = nacosServiceManager.getNamingService();
        namingService.registerInstance(serverName, InetAddress.getLocalHost().getHostAddress(), this.port);
    }

    public void run() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new WebSocketChannelInitializer());

        ChannelFuture future = bootstrap.bind(port).sync();
        channel = future.channel();
        log.info("WebSocket Server started at port :{}", port);
    }

    @PreDestroy // Spring Bean销毁前执行
    public void stop() {
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println("WebSocket Server stopped");
    }

    private class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) {
            System.out.println("初始化客户端连接: " + ch.remoteAddress());

            ChannelPipeline pipeline = ch.pipeline();
            // 心跳
            pipeline.addLast(new IdleStateHandler(5 * 60, 0, 0));
            // HTTP编解码器
            pipeline.addLast(new HttpServerCodec());
            // 聚合HTTP完整报文
            pipeline.addLast(new HttpObjectAggregator(65536));
            // 设置uuid和token
            pipeline.addLast(new WebSocketTokenAuthHeader());
            // WebSocket协议处理器
            pipeline.addLast(new WebSocketServerProtocolHandler("/api/v1/netty", null, true));
            // 自定义业务处理器
            pipeline.addLast(new MessageInboundHandler(redisTemplate));
        }
    }
}
