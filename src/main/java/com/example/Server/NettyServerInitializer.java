package com.example.Server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-27  15:36
 */
@Component
@Slf4j
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    private NettyHandler nettyHandler;

    //连接注册，创建成功，会被调用
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("==================netty报告==================");
        log.info("信息：有一客户端链接到本服务端");
        log.info("IP:{}", ch.remoteAddress().getAddress());
        log.info("Port:{}", ch.remoteAddress().getPort());
        log.info("通道id:{}", ch.id().toString());
        log.info("==================netty报告完毕==================");
        ChannelPipeline pipeline = ch.pipeline();
        //定义读写空闲时间——（单位秒）
        pipeline.addLast(new IdleStateHandler(180, 60,180));
        //注册拦截器
        pipeline.addLast(nettyHandler);
    }
}
