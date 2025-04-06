package com.dofinal.RG.util;

import com.dofinal.RG.entity.user.UserNotice;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Classname WebSocketServer
 * Description TODO
 * Date 2024/5/28 21:50
 * Created ZHW
 */

@Component
@ServerEndpoint("/websocket")
public class WebSocketServer {
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> userSessionMap = new ConcurrentHashMap<>();
    private Session session;
    private static ObjectMapper objectMapper = new ObjectMapper(); // 静态实例
    @Autowired
    NoticeUtil noticeUtil;

    public WebSocketServer() {
        this.noticeUtil = SpringContextHolder.getBean(NoticeUtil.class);
    }

    @OnOpen
    @Trace
    public void onOpen(Session session) {
        this.session = session;
        webSocketMap.put(session.getId(), this);
        System.out.println("New connection opened, session ID: " + session.getId());
    }

    @OnClose
    @Trace
    public void onClose(Session session) {
        webSocketMap.remove(session.getId());
        System.out.println("Connection closed, session ID: " + session.getId());
    }

    @OnError
    @Trace
    public void onError(Session session, Throwable error) {
        System.err.println("Error from session ID: " + session.getId() + " Error: " + error.getMessage());
        error.printStackTrace();
    }

    @OnMessage
    @Trace
    public void onMessage(String message, Session session) {
        if (message.startsWith("a_")) {
            handleMessageTypeA(message.substring(2), session); // 去除前缀后处理类型A的消息
        } else if (message.startsWith("b_")) {
            handleMessageTypeB(message.substring(2), session); // 去除前缀后处理类型B的消息
        } else if (message.startsWith("c_")) {
            handleMessageTypeC(message.substring(2), session);
        } else {
            System.out.println("Received unknown message type from client: " + message);
        }
    }

    @Trace
    public void handleMessageTypeA(String message, Session session) {
        // 处理类型A的消息逻辑
        userSessionMap.put(message, session.getId());
        System.out.println("Handling message type A: " + message);
        // ... 具体操作
    }

    @Trace
    public void handleMessageTypeB(String message, Session session) {
        // 处理类型B的消息逻辑
        noticeUtil.updateNotice(Integer.parseInt(message));
        System.out.println("Handling message type B: " + message);
        noticeUtil.updateNotice(Integer.parseInt(message));
    }
    @Trace
    public void handleMessageTypeC(String message, Session session) {
        // 处理类型B的消息逻辑
        noticeUtil.deleteNotice(Integer.parseInt(message));
        System.out.println("Handling message type C: " + message);
        // ... 具体操作
    }

    @Trace
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    @Trace
    public void sendCustomMessage(UserNotice customMessage) throws IOException {
        try {
            String message = objectMapper.writeValueAsString(customMessage);
            sendMessage(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IOException("Failed to serialize custom message", e);
        }
    }

    @Trace
    public static void sendInfo(String message, String userId) throws IOException {
        if (userId != null && !userId.isEmpty()) {
            if (webSocketMap.containsKey(userId)) {
                webSocketMap.get(userId).sendMessage(message);
            } else {
                System.out.println("用户不在线！");
            }
        } else {
            // userId 为空，广播消息给所有连接的用户
            for (WebSocketServer webSocketServer : webSocketMap.values()) {
                webSocketServer.sendMessage(message);
            }
        }
    }

    @Trace
    public static void sendCustomInfo(UserNotice customMessage, String uid) throws IOException {
        String userSession = userSessionMap.get(uid);
        if (userSession != null && !userSession.isEmpty()) {
            if (webSocketMap.containsKey(userSession)) {
                webSocketMap.get(userSession).sendCustomMessage(customMessage);
            } else {
                System.out.println("用户不在线！");
            }
        } else {
            // userId 为空，广播消息给所有连接的用户
            for (WebSocketServer webSocketServer : webSocketMap.values()) {
                webSocketServer.sendCustomMessage(customMessage);
            }
        }
    }
}

