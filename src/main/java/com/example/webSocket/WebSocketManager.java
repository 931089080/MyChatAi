package com.example.webSocket;



import com.example.model.RoleContent;
import com.example.model.UserMessage;
import jakarta.websocket.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-16  10:18
 */
public class WebSocketManager {

    private static final Map<String, Session> sessions = new HashMap<>();
    public static Map<String, List<RoleContent>> historyLists = new HashMap<>(); // 对话历史存储集合

    public static final Map<String, List<UserMessage>> USER_MESSAGES_MAP = new HashMap<>();


    public static void addSession(String endpoint, Session session){
        sessions.put(endpoint, session);
    }

    public static Session getSession(String endpoint) {
        return sessions.get(endpoint);
    }

    public static Session removeSession(String endpoint) {
        return sessions.remove(endpoint);
    }

}
