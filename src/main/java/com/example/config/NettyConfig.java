package com.example.config;

import com.example.handler.SparkAiNettyInboundHandler;
import com.example.properties.SparkAiProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-23  16:12
 */
@Configuration
public class NettyConfig {
    private final SparkAiProperties sparkAiProperties;

    public NettyConfig(SparkAiProperties sparkAiProperties) {
        this.sparkAiProperties = sparkAiProperties;
    }

    @Bean
    public Bootstrap nettyBootstrap() {
        // Configure SSL context
//        SslContext sslContext = SslContextBuilder.forClient().build();
//        String urlStr = sparkAiProperties.sseUrl;
//        boolean isSSL = urlStr.contains("https");

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws SSLException {
                        ch.config().setKeepAlive(true);
                        ch.config().setTcpNoDelay(true);
//                        if (isSSL) { //配置Https通信
//                            SslContext context = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
//                            ch.pipeline().addLast(context.newHandler(ch.alloc()));
//                        }

                        ChannelPipeline pipeline = ch.pipeline();
//                        SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
//                        pipeline.addLast(new SslHandler(sslEngine));
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new HttpContentCompressor());
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpContentDecompressor());
                        pipeline.addLast(new SparkAiNettyInboundHandler());
                    }
                });
        return bootstrap;
    }

}
