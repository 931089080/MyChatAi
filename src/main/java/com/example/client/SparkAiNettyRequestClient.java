package com.example.client;

import com.example.properties.SparkAiProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-23  16:46
 */
@Service
public class SparkAiNettyRequestClient {
    private final Bootstrap bootstrap;
    private final SparkAiProperties sparkAiProperties;

    public SparkAiNettyRequestClient(Bootstrap bootstrap, SparkAiProperties sparkAiProperties) {
        this.bootstrap = bootstrap;
        this.sparkAiProperties = sparkAiProperties;
    }

    public void connectToSse() throws InterruptedException {
        URI uri = URI.create(sparkAiProperties.getSseUrl());
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? 443 : uri.getPort(); // HTTPS 默认端口是 443

        ChannelFuture future = bootstrap.connect(host, port).sync();

        // Build the HTTP request
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.ACCEPT, "application/json");
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        request.headers().set(HttpHeaderNames.AUTHORIZATION, "Bearer " + sparkAiProperties.getAPIPassword());

        // Example: Add a JSON payload
        String jsonPayload = "{\"key\": \"value\"}";
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, jsonPayload.length());

        // Send the request
        future.channel().writeAndFlush(request).sync();

        // Write the JSON payload
        future.channel().writeAndFlush(Unpooled.copiedBuffer(jsonPayload, CharsetUtil.UTF_8)).sync();

        // Wait for the connection to close
        future.channel().closeFuture().sync();
    }
}
