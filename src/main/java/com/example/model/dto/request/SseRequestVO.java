package com.example.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-20  21:40
 */
@Data
@AllArgsConstructor
public class SseRequestVO {
    private String model;
    private List<MessageVO> messages;
    private Boolean stream;
}
