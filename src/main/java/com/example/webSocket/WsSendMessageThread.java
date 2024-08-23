package com.example.webSocket;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.model.RoleContent;
import com.example.properties.SparkAiProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author: owo
 * @version: 1.0
 * @description: 开一个线程发送问题
 * @date: 2024-08-16  18:05
 */
@Slf4j
public class WsSendMessageThread extends Thread {

    private static final SparkAiProperties sparkAiProperties = SpringUtil.getBean(SparkAiProperties.class);

    private WebSocket webSocket;
    private String newQuestion;
    private String userId;

    public WsSendMessageThread(WebSocket webSocket, String newQuestion, String userId) {
        this.webSocket = webSocket;
        this.newQuestion = newQuestion;
        this.userId = userId;
    }

    public void run() {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();  // header参数
            header.put("app_id", sparkAiProperties.getAppid());
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

            JSONObject parameter = new JSONObject(); // parameter参数
            JSONObject chat = new JSONObject();
            chat.put("domain", sparkAiProperties.domain);
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 4096);
            parameter.put("chat", chat);

            JSONObject payload = new JSONObject(); // payload参数
            JSONObject message = new JSONObject();
            JSONArray text = new JSONArray();

            // 历史问题获取
            List<RoleContent> historyList = WebSocketManager.historyLists.get(userId);
            if (historyList.size() > 0) {
                for (RoleContent tempRoleContent : historyList) {
                    text.add(JSON.toJSON(tempRoleContent));
                }
            }

            // 最新问题
            RoleContent roleContent = new RoleContent();
            roleContent.setRole("user");
            roleContent.setContent(newQuestion);
            text.add(JSON.toJSON(roleContent));
            historyList.add(roleContent);

            message.put("text", text);
            payload.put("message", message);

            requestJson.put("header", header);
            requestJson.put("parameter", parameter);
            requestJson.put("payload", payload);

            log.info("请求参数 -> {}", requestJson);
            webSocket.send(requestJson.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
