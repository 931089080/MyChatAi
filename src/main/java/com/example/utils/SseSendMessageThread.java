package com.example.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.client.SseClient;
import com.example.manager.SseManager;
import com.example.model.dto.request.MessageVO;
import com.example.model.vo.ClientToMessageVo;
import com.example.properties.SparkAiProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-20  22:04
 */
@Slf4j
@AllArgsConstructor
public class SseSendMessageThread extends Thread {

    private static final SparkAiProperties sparkAiProperties = SpringUtil.getBean(SparkAiProperties.class);

    private SseClient sseClient;
    private ClientToMessageVo clientToMessageVo;

    public static String totalAnswer = ""; // 大模型的答案汇总

    @Override
    public void run() {
        //获取历史聊天记录
        List<MessageVO> historyList = SseManager.historyList.getOrDefault(clientToMessageVo.getClientId(), new ArrayList<>());
        if (historyList.isEmpty()) {
            //为空 新会话 首次发送请求
            SseManager.historyList.put(clientToMessageVo.getClientId(), historyList);
        }

        JSONArray messages = new JSONArray();
        //有历史消息 放入请求体中
        if (historyList.size() > 0) {
            for (MessageVO list : historyList) {
                messages.add(list);
            }
        }
        MessageVO messageVO = new MessageVO("user", clientToMessageVo.getData());
        messages.add(messageVO);

        JSONObject requestJson = new JSONObject();
        requestJson.put("model", sparkAiProperties.getDomain());
        requestJson.put("messages", messages);
        requestJson.put("stream", true);
        log.info("请求body -> {}", requestJson);

        //构建请求
        Request request = new Request.Builder()
                .url(sparkAiProperties.getSseUrl())
                .post(okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJson.toString()))
                .addHeader("Authorization", "Bearer " + sparkAiProperties.getAPIPassword())
                .build();

        //设置sse监听回调
        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                if ("[DONE]".equals(data)) {
                    SseManager.historyList.get(clientToMessageVo.getClientId()).add(new MessageVO("assistant", totalAnswer));
                    return;
                }

                JSONObject jsonObject = JSON.parseObject(data);
                Integer code = jsonObject.getInteger("code");
                if (code == 0) {
                    JSONArray choices = jsonObject.getJSONArray("choices");
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject delta = choice.getJSONObject("delta");
                    String content = delta.getString("content");
                    //打印接收ai 解析的消息
                    System.out.print(content);
                    sseClient.sendMessageToOneClient(clientToMessageVo.getClientId(), content);
                    totalAnswer += content;
                }
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                System.out.println();
                log.warn("onFailure -> {}", request);
                log.warn("response -> {}", response);
                try {
                    String body = response.body().string();
                    log.warn("body -> {}", body);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                log.warn("closeConnect");
                SseManager.historyList.remove(clientToMessageVo.getClientId());
                sseClient.closeConnect(clientToMessageVo.getClientId());
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                System.out.println();
                log.warn("SseSendMessageThread.onClosed");
                sseClient.closeConnect(clientToMessageVo.getClientId());
            }
        };

        OkHttpClient client = new OkHttpClient();
        EventSource.Factory factory = EventSources.createFactory(client);
        // 创建事件
        EventSource eventSource = factory.newEventSource(request, listener);

        canAddHistory(historyList, messageVO);
//        log.info("sse历史消息 -> {}", SseManager.historyList.get(clientToMessageVo.getClientId()));
    }

    //历史记录最大上线1.2W左右
    public static void canAddHistory(List<MessageVO> historyList, MessageVO messageVO) {
        int history_length = 0;
        for (MessageVO temp : historyList) {
            history_length = history_length + temp.getContent().length();
        }
//        log.info("当前历史记录长度 -> {}", history_length);
        // 删除前面记录会把相应的内存空间也删除 并且下标前移
        if (history_length > 12000) {
            for (int i = 0; i < 5; i++) {
                historyList.remove(i);
            }
        }
        //存入发送历史消息
        historyList.add(messageVO);
    }

}
