package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-17  12:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessage {

    private String problem;

    private List<String> msg;

}
