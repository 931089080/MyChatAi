package com.example.model.dto;

import com.example.webSocket.BigModelNew;
import lombok.Data;

/**
 * @author: owo
 * @version: 1.0
 * @description: 返回的json结果拆解
 * @date: 2024-08-17  17:22
 */
@Data
public class JsonParse {
    private Header header;
    private Payload payload;
}
