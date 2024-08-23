package com.example.controller;

import com.example.client.SseClient;
import com.example.manager.SseManager;
import com.example.model.vo.ClientToMessageVo;
import com.example.utils.SseSendMessageThread;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-20  09:34
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/sse")
public class AiSseController {

    @Resource
    private SseClient sseClient;

    /**
     * 接收客户端发送的消息 通过sse响应给客户端 并关闭sse流
     *
     * @param clientToMessageVo
     */
    @PostMapping("/AI")
    public SseEmitter sendMessageToChat(@RequestBody(required = false) ClientToMessageVo clientToMessageVo) {
        if (clientToMessageVo.getClientId().isEmpty()) {
            return null;
        }
        SseEmitter connect = sseClient.createConnect(clientToMessageVo.getClientId());

        new SseSendMessageThread(sseClient, clientToMessageVo).start();

        return connect;
    }

    @GetMapping("/createConnect/{clientId}")
    public SseEmitter createConnect(@PathVariable("clientId") String clientId) {
        return sseClient.createConnect(clientId);
    }

    @PostMapping("/broadcast")
    public void sendMessageToAllClient(@RequestBody(required = false) String msg) {
        sseClient.sendMessageToAllClient(msg);
    }

    @PostMapping("/sendMessage")
    public void sendMessageToOneClient(@RequestBody(required = false) ClientToMessageVo clientToMessageVo) {
        if (clientToMessageVo.getClientId().isEmpty()) {
            return;
        }
        sseClient.sendMessageToOneClient(clientToMessageVo.getClientId(), clientToMessageVo.getData());
    }

    @GetMapping("/closeConnect")
    public void closeConnect(@RequestParam(required = true) String clientId) {
        sseClient.closeConnect(clientId);
    }
}
