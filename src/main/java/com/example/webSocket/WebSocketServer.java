package com.example.webSocket;

import cn.hutool.extra.spring.SpringUtil;
import com.example.model.RoleContent;
import com.example.properties.SparkAiProperties;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: owo
 * @version: 1.0
 * @description: 客户端连接ws服务 并开启线程发送问题给三方接口
 * @date: 2024-08-15  11:14
 */
@Slf4j
@Component
@ServerEndpoint("/AI/{userId}")
public class WebSocketServer {

    private String requestUrl;

    private static WebSocket webSocket = null; // 大模型的答案汇总

    private static final SparkAiProperties sparkAiProperties = SpringUtil.getBean(SparkAiProperties.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        log.info("客户端 onOpen ->  {}", userId);
        WebSocketManager.historyLists.put(userId, new ArrayList<>());
        WebSocketManager.addSession(userId, session);

        // 构建鉴权url
        try {

            String authUrl = getAuthUrl(sparkAiProperties.getWsUrl(), sparkAiProperties.getApiKey(), sparkAiProperties.getApiSecret());
            requestUrl = authUrl.replace("http://", "ws://").replace("https://", "wss://");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        log.info("接收客户端 onMessage -> {}", message);
        log.info("历史消息 -> {}", WebSocketManager.historyLists.get(userId));

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(requestUrl).build();
        webSocket = client.newWebSocket(request, new BigModelNew(userId, message));

        WsSendMessageThread wsSendMessageThread = new WsSendMessageThread(webSocket, message, userId);
        wsSendMessageThread.start();
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        log.warn("客户端 OnClose -> " + WebSocketManager.getSession(userId));
        WebSocketManager.removeSession(userId);
        WebSocketManager.historyLists.remove(userId);
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
//        URL url = new URL(hostUrl);
        URI url = new URI(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).
                addQueryParameter("date", date).
                addQueryParameter("host", url.getHost()).
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

}
