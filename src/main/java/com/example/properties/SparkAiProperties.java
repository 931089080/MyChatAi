package com.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-15  21:11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spark-ai")
public class SparkAiProperties {

    public String wsUrl;
    public String sseUrl;
    public String appid;
    public String apiSecret;
    public String apiKey;
    public String domain;
    public String APIPassword;

}
