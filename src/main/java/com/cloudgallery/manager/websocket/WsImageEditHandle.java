package com.cloudgallery.manager.websocket;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import com.cloudgallery.manager.websocket.model.ImageEditActionEnums;
import com.cloudgallery.manager.websocket.model.ImageEditMessageDto;
import com.cloudgallery.manager.websocket.model.ImageEditMessageTypeEnums;
import com.cloudgallery.manager.websocket.model.ImageEditMessageVo;
import com.cloudgallery.model.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsImageEditHandle extends TextWebSocketHandler {
    // 存储所有连接的session
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> SESSION_MAP = new ConcurrentHashMap<>();
    // 存储所有正在编辑的用户ID
    private final ConcurrentHashMap<Long, Long> EDIT_USER_MAP = new ConcurrentHashMap<>();

    /**
     * 连接建立成功后执行
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 获取连接信息
        Long imageId = (Long)session.getAttributes().get("imageId");
        User user = (User) session.getAttributes().get("user");
        // 添加 session 到 SESSION_MAP
        SESSION_MAP.putIfAbsent(imageId,ConcurrentHashMap.newKeySet());
        SESSION_MAP.get(imageId).add(session);
        // 广播用户加入信息
        ImageEditMessageVo message = new ImageEditMessageVo();
        message.setType(ImageEditMessageTypeEnums.INFO.getValue());
        message.setMessage(String.format("用户 %s 加入编辑", user.getName()));
        broadcastMessage(user,imageId, message);
    }

    /**
     * 接收到消息后执行
     * @param session
     * @param message
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获取连接信息
        Long imageId = (Long)session.getAttributes().get("imageId");
        User user = (User) session.getAttributes().get("user");
        // 获取请求Dto，解析得到事件消息
        ImageEditMessageDto imageEditMessageDto = BeanUtil.copyProperties(message.getPayload(),
                ImageEditMessageDto.class);
        String type = imageEditMessageDto.getType();
        String editAction = imageEditMessageDto.getEditAction();
        ImageEditMessageTypeEnums typeEnums = ImageEditMessageTypeEnums.getByValue(type);
        // 根据事件消息类型进行不同处理
        switch (typeEnums){
            case ENTER_EDIT -> handleEnterMessage(user, imageId);
            case EXIT_EDIT -> handleExitMessage(user, imageId);
            case EDIT_ACTION -> handleEditActionMessage(user, imageId, editAction);
            default -> {
                ImageEditMessageVo messageVo = new ImageEditMessageVo();
                messageVo.setType(ImageEditMessageTypeEnums.ERROR.getValue());
                messageVo.setMessage(String.format("用户 %s 发送错误信息", user.getName()));
                broadcastMessage(user,imageId, messageVo);
            }
        }
    }


    /**
     * 连接关闭后执行
     * @param session
     * @param status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 关闭连接时判断编辑用户信息
        Long imageId = (Long)session.getAttributes().get("imageId");
        User user = (User) session.getAttributes().get("user");
        handleExitMessage(user, imageId);
        // 移除用户连接信息
        SESSION_MAP.get(imageId).remove(session);
        if (SESSION_MAP.get(imageId).size() == 0) {
            SESSION_MAP.remove(imageId);
        }
        // 广播用户退出信息
        ImageEditMessageVo messageVo = new ImageEditMessageVo();
        messageVo.setType(ImageEditMessageTypeEnums.INFO.getValue());
        messageVo.setMessage(String.format("用户 %s 退出房间", user.getName()));
        broadcastMessage(user,imageId, messageVo);
    }

    /**
     * 广播信息给房间内所有用户（除自己）
     * @param loginUser
     * @param imageId
     * @param messageVo
     */
    public void broadcastMessage(User loginUser,Long imageId, ImageEditMessageVo messageVo) throws Exception {
        Set<WebSocketSession> webSocketSessions = SESSION_MAP.get(imageId);
        if(webSocketSessions!=null){
            for (WebSocketSession webSocketSession : webSocketSessions) {
                // 跳过自己
                if(webSocketSession.getAttributes().get("userId").equals(loginUser.getId())){
                    continue;
                }
                // 序列化发送消息为Json
                ObjectMapper objectMapper = new ObjectMapper();
                TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(messageVo));
                webSocketSession.sendMessage(textMessage);
            }
        }
    }

    /**
     * 编辑事件操作
     * @param user
     * @param imageId
     * @param editAction
     */
    private void handleEditActionMessage(User user, Long imageId, String editAction) throws Exception {
        // 判断当前编辑用户是否是自己
        Long editUserId = EDIT_USER_MAP.get(imageId);
        if (editUserId != null && editUserId.equals(user.getId())) {
            // 获取具体操作事件
            ImageEditActionEnums actionEnums = ImageEditActionEnums.getByValue(editAction);
            if (actionEnums == null) return;
            // 广播事件信息
            ImageEditMessageVo messageVo = new ImageEditMessageVo();
            messageVo.setType(ImageEditMessageTypeEnums.EDIT_ACTION.getValue());
            messageVo.setEditAction(actionEnums.getValue());
            messageVo.setMessage(String.format("用户 %s 执行 %s", user.getName(), actionEnums.getName()));
            broadcastMessage(user, imageId, messageVo);
        }
    }

    /**
     * 用户退出编辑状态
     * @param user
     * @param imageId
     */
    private void handleExitMessage(User user, Long imageId) throws Exception {
        // 判断当前编辑用户是否是自己
        Long editUserId = EDIT_USER_MAP.get(imageId);
        if (editUserId != null && editUserId.equals(user.getId())) {
            EDIT_USER_MAP.remove(imageId);
            ImageEditMessageVo messageVo = new ImageEditMessageVo();
            messageVo.setType(ImageEditMessageTypeEnums.EXIT_EDIT.getValue());
            messageVo.setMessage(String.format("用户 %s 退出编辑", user.getName()));
            broadcastMessage(user, imageId, messageVo);
        }
    }

    /**
     * 用户请求进入编辑状态
     * @param user
     * @param imageId
     */
    private void handleEnterMessage(User user, Long imageId) throws Exception {
        // 判断是否有用户在编辑
        if (!EDIT_USER_MAP.containsKey(imageId)) {
            // 设置当前用户为编辑用户
            EDIT_USER_MAP.putIfAbsent(imageId, user.getId());
            // 广播消息
            ImageEditMessageVo messageVo = new ImageEditMessageVo();
            messageVo.setType(ImageEditMessageTypeEnums.ENTER_EDIT.getValue());
            messageVo.setMessage(String.format("用户 %s 进入编辑状态", user.getName()));
            broadcastMessage(user, imageId, messageVo);
        } else {
            ImageEditMessageVo messageVo = new ImageEditMessageVo();
            messageVo.setType(ImageEditMessageTypeEnums.INFO.getValue());
            messageVo.setMessage(String.format("用户 %s 正在编辑，请等待",EDIT_USER_MAP.get(imageId)));
            broadcastMessage(user, imageId, messageVo);
        }

    }

}
