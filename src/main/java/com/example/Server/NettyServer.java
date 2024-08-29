package com.example.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-27  15:33
 */
@Component
@Slf4j
public class NettyServer {
    @Autowired
    private NettyServerInitializer nettyServerInitializer;
    @Value("${nettyPort}")
    private Integer nettyPort;
    @PostConstruct
    public void serverRun() {
        //循环组接收连接，不进行处理,转交给下面的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //循环组处理连接，获取参数，进行工作处理
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //服务端进行启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //使用NIO模式，初始化器等等
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(nettyServerInitializer);
            //绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(nettyPort).sync();
            log.info("tcp服务器已经启动…………");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
