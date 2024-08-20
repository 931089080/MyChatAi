package com.example.controller;

import com.example.client.SseClient;
import com.example.model.vo.MessageVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-20  09:34
 */
@CrossOrigin
@RestController
@RequestMapping("/sse")
public class AiController {

    @Resource
    private SseClient sseClient;

    @GetMapping("/createConnect/{clientId}")
    public SseEmitter createConnect(@PathVariable("clientId") String clientId) {
        return sseClient.createConnect(clientId);
    }

    @PostMapping("/broadcast")
    public void sendMessageToAllClient(@RequestBody(required = false) String msg) {
        sseClient.sendMessageToAllClient(msg);
    }

    @PostMapping("/sendMessage")
    public void sendMessageToOneClient(@RequestBody(required = false) MessageVo messageVo) {
        if (messageVo.getClientId().isEmpty()) {
            return;
        }
        sseClient.sendMessageToOneClient(messageVo.getClientId(), messageVo.getData());
    }

    @GetMapping("/closeConnect")
    public void closeConnect(@RequestParam(required = true) String clientId) {
        sseClient.closeConnect(clientId);
    }
}
