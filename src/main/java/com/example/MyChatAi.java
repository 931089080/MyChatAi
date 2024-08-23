package com.example;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
public class MyChatAi {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MyChatAi.class).build().run();
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