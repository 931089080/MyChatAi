package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-17  17:21
 */
@Data
@AllArgsConstructor
public class Text {

    private Integer status;
    private String content;
}
