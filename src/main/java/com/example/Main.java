package com.example;


import com.example.model.UserMessage;
import com.example.properties.SparkAiProperties;
import com.example.webSocket.WebSocketManager;
import com.example.webSocket.WebSocketServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

@EnableConfigurationProperties
@SpringBootApplication
@EnableScheduling
public class Main {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Main.class).build().run();
//        SpringApplication.run(Main.class, args);
    }

//    @Scheduled(fixedDelay = 8000)
//    public void print() {
//        for (Map.Entry<String, List<UserMessage>> entry : WebSocketManager.USER_MESSAGES_MAP.entrySet()) {
//            System.out.println();
//            System.out.println("userId:" + entry.getKey() + " -> " + entry.getValue());
//        }
//    }
}