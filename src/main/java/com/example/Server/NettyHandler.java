package com.example.Server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author:hejz 75412985@qq.com
 * @create: 2023-01-24 22:51
 * @Description: 注册拦截器——每个设备都需要注册
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class NettyHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //读到的客户端信息的逻辑处理（略）
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.getCause();
//        NettyServiceCommon.deleteKey(ctx.channel());
        ctx.channel().close();
    }
}
