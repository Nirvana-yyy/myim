package com.example.myim.ws;

import com.example.myim.utils.EnhancedThreadFactory;
import com.example.myim.ws.handler.CloseIdleChannelHandler;
import com.example.myim.ws.handler.WebsocketRouterHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author YJL
 */
@Component
@Slf4j
public class WebSocketServer {

    @Autowired
    private ServerConfig serverConfig;

    private ServerBootstrap serverBootstrap;

    private ChannelFuture channelFuture;

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    @Autowired
    private CloseIdleChannelHandler closeIdleChannelHandler;

    //处理耗时任务的线程
    private EventExecutorGroup eventExecutorGroup;

    @PostConstruct
//    PostConstruct在构造函数之后执行，init()方法之前执行。
    public void start() {
        if (serverConfig.port == 0){
            log.warn("webSocket Server not config");
            return ;
        }
        log.info("websocket Server is starting");
        //参数：线程数，线程工厂类
        eventExecutorGroup = new DefaultEventExecutorGroup(serverConfig.userThreads,new EnhancedThreadFactory("WebSocketBizThreadPool"));
        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //先添加websocket相关的编解码器和协议处理器
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                pipeline.addLast(new WebSocketServerProtocolHandler("/",null,true));
                //再添加服务端业务消息的总处理器
                pipeline.addLast(websocketRouterHandler);
                //服务端添加一个idle处理器，如果一段时间socket中没有消息传输，服务端就会强制断开
                pipeline.addLast(new IdleStateHandler(0,0, serverConfig.getAllIdleSecond()));
                pipeline.addLast(closeIdleChannelHandler);

            }
        };
        serverBootstrap = newServerBootstrap();
        serverBootstrap.childHandler(initializer);

        try {
            channelFuture = serverBootstrap.bind(serverConfig.port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        log.info("WebSocket Server start success on: "+serverConfig.port);
        new Thread(()->{
            try {
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("WebSocket Server start failed",e);
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * 如果系统本省支持epoll同时用户自己的配置也允许epoll。会优先使用EpollEventGroup
     * @return
     */
    public ServerBootstrap newServerBootstrap(){
        if (Epoll.isAvailable() && serverConfig.useEpoll){
            EventLoopGroup bossGroup =
                    new EpollEventLoopGroup(serverConfig.bossThreads,new DefaultThreadFactory("WebSocketBossGroup",true));
            EventLoopGroup workerGroup =
                    new EpollEventLoopGroup(serverConfig.workerThreads,new DefaultThreadFactory("WebSocketWorkerGroup",true));
            return new ServerBootstrap().group(bossGroup,workerGroup).channel(EpollServerSocketChannel.class);

        }
        return newNioServerBootstrap(serverConfig.bossThreads,serverConfig.workerThreads);
    }

    private ServerBootstrap newNioServerBootstrap(int bossThreads, int workerThreads) {
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        if (bossThreads >= 0 && workerThreads >= 0){
            bossGroup = new NioEventLoopGroup(bossThreads);
            workerGroup = new NioEventLoopGroup(workerThreads);
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
        }
        return new ServerBootstrap().group(bossGroup,workerGroup).channel(NioServerSocketChannel.class);
    }

    class ShutdownThread extends Thread {
        @Override
        public void run(){
            close();
        }
    }

    public void close(){
        if (serverBootstrap == null){
            log.info("WebSocket server is not running");
            return ;
        }
        log.info("WebSocket server is stopping");
        if (channelFuture != null){
            channelFuture.channel().close().awaitUninterruptibly(10, TimeUnit.SECONDS);
            channelFuture = null;
        }
        if (serverBootstrap != null && serverBootstrap.config().group() !=  null){
            serverBootstrap.config().group().shutdownGracefully();
        }
        if(serverBootstrap != null && serverBootstrap.config().childGroup() != null){
            serverBootstrap.config().childGroup().shutdownGracefully();
        }
        serverBootstrap = null;
        log.info("WebSocket server stopped");
    }




}
