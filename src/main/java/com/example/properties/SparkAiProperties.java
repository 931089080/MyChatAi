package com.example.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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

    public String hostUrl;
    public String appid = "6a108640";
    public String apiSecret = "MjJjNWYwNTkzNjY4ZGFmNGU1NmE5YzFk";
    public String apiKey = "b4a1c577a71b11e4a5a3074ced6a4341";
    public String domain = "4.0Ultra"; //取值为[general,generalv2,generalv3,pro-128k,generalv3.5,4.0Ultra]

}
