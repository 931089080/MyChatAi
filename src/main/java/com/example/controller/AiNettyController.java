package com.example.controller;

import com.example.client.SparkAiNettyRequestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-23  17:27
 */
@RestController
@RequestMapping("/netty-sse")
public class AiNettyController {

    private final SparkAiNettyRequestClient SparkAiNettyRequestClient;

    public AiNettyController(SparkAiNettyRequestClient SparkAiNettyRequestClient) {
        this.SparkAiNettyRequestClient = SparkAiNettyRequestClient;
    }

    @GetMapping
    public String startSse() {
        try {
            SparkAiNettyRequestClient.connectToSse();
            return "SSE connection started";
        } catch (InterruptedException e) {
            return "Failed to start SSE connection";
        }
    }
}
