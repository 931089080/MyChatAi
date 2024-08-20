package com.example.webSocket;

import com.alibaba.fastjson.JSON;
import com.example.model.RoleContent;
import com.example.model.UserMessage;
import com.example.model.dto.JsonParse;
import com.example.model.dto.Text;
import com.google.gson.Gson;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ObjectUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: owo
 * @version: 1.0
 * @description: 接收三方ws接口消息
 * @date: 2024-08-17  18:05
 */
@Slf4j
public class BigModelNew extends WebSocketListener {

    public static List<RoleContent> historyList = new ArrayList<>(); // 对话历史存储集合
    public static final Gson gson = new Gson();

    public static String totalAnswer = ""; // 大模型的答案汇总

    private Session clientSession = null;
    // 个性化参数
    private String userId;
    private final String problem;

    // 构造函数
    public BigModelNew(String userId, String problem) {
        this.userId = userId;
        this.problem = problem;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        log.info("BigModelNew.onOpen 与三方接口建立连接");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // System.out.println(userId + "用来区分那个用户的结果" + text);
        JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
        if (myJsonParse.getHeader().getCode() != 0) {
            System.out.println("发生错误，错误码为：" + myJsonParse.getHeader().getCode());
            System.out.println("本次请求的sid为：" + myJsonParse.getHeader().getSid());
            webSocket.close(1000, "");
        }

        List<Text> textList = myJsonParse.getPayload().getChoices().getText();
        for (Text temp : textList) {
            //打印答案
            System.out.print(temp.getContent());
            clientSession = WebSocketManager.getSession(userId);
            try {
                //发送答案给客户端
//                clientSession.getBasicRemote().sendText(temp.getContent());
                temp.setStatus(1);
                log.info("JSON.toJSONString(temp) -> {}", JSON.toJSONString(temp));
                clientSession.getBasicRemote().sendText(JSON.toJSONString(temp));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            totalAnswer = totalAnswer + temp.getContent();
            List<UserMessage> problem = WebSocketManager.USER_MESSAGES_MAP.getOrDefault(userId, new ArrayList<>());
            UserMessage userMessage = problem.isEmpty() ? new UserMessage() : problem.get(problem.size() - 1);
            userMessage.setProblem(this.problem);

            List<String> msg = userMessage.getMsg();
            if (ObjectUtils.isEmpty(msg)) {
                msg = new ArrayList<>();
            }

            msg.add(temp.getContent());
            userMessage.setMsg(msg);

            problem.remove(userMessage);
            problem.add(userMessage);
            WebSocketManager.USER_MESSAGES_MAP.put(userId, problem);
        }

        if (myJsonParse.getHeader().getStatus() == 2) {
            try {
                clientSession.getBasicRemote().sendText(JSON.toJSONString(new Text(-1, null)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 关闭三方ws连接，释放资源
            webSocket.close(1000, "Connection closing normally.");

            System.out.println();
            System.out.println("---------------------------------------------------------------------------------------------------");
            if (canAddHistory()) {
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            } else {
                historyList.remove(0);
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        log.warn("BigModelNew.onFailure");
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
        log.warn("BigModelNew.onClosed");
    }

    // 由于历史记录最大上线1.2W左右，需要判断是能能加入历史
    public static boolean canAddHistory() {
        int history_length = 0;
        for (RoleContent temp : historyList) {
            history_length = history_length + temp.getContent().length();
        }
        if (history_length > 12000) {
            historyList.remove(0);
            historyList.remove(1);
            historyList.remove(2);
            historyList.remove(3);
            historyList.remove(4);
            return false;
        } else {
            return true;
        }
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
        String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "GET " + url.getPath() + " HTTP/1.1";
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
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).addQueryParameter("date", date).addQueryParameter("host", url.getHost()).build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

}