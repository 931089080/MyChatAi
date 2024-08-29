package com.example.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;

/**
 * @author: owo
 * @version: 1.0
 * @description: Netty入站处理
 * @date: 2024-08-23  20:55
 */
public class SparkAiNettyInboundHandler extends ChannelInboundHandlerAdapter {

    //        private final ObjectMapper objectMapper = new ObjectMapper();
    private final StringBuilder responseContent = new StringBuilder();

    /**
     * 当通道（Channel）接收到数据时。
     * 每当从网络中读取到数据并且它被传递到 ChannelPipeline 中的一个处理程序（ChannelHandler）时，channelRead 方法会被调用。
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            HttpHeaders headers = response.headers();
            System.out.println("Response status: " + response.status());
            System.out.println("Response headers: " + headers);
        }

        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            responseContent.append(content.content().toString(io.netty.util.CharsetUtil.UTF_8));

            if (content instanceof LastHttpContent) {
                parseJson(responseContent.toString());
                ctx.close();
            }
        }
    }

    private void parseJson(String json) {
        try {
//                JsonNode jsonNode = objectMapper.readTree(json);

            System.out.println("Parsed JSON: " + json.toString());
            // 你可以根据你的需求处理 JSON 数据
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
