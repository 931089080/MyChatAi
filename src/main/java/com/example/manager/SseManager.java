package com.example.manager;

import com.example.model.dto.request.MessageVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-22  09:37
 */
public class SseManager {

    public static Map<String, List<MessageVO>> historyList = new HashMap<>();

}
