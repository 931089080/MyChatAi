package com.example.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-20  21:43
 */
@Data
@AllArgsConstructor
public class MessageVO {
    private String role;
    private String content;
}
