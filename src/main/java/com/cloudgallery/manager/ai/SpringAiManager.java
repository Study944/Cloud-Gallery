package com.cloudgallery.manager.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

/**
 * AI管理器
 */
@Component
@Slf4j
public class SpringAiManager {
    // 根据图片生成描述，并且写入数据库对应图片的描述中
    private final ChatClient chatClient;
    // 根据用于提示词生成图片，并且保存到COS和数据库中
    private final ImageModel imageModel;

    private SpringAiManager(ChatModel dashscopeChatModel, ImageModel imageModel) {
        chatClient = ChatClient.builder(dashscopeChatModel).build();
        this.imageModel = imageModel;
    }

    public String generationImageDescription(String prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    public String generateImage(String prompt) {
        ImageResponse response = imageModel.call(new ImagePrompt(prompt));
        return response.getResult().getOutput().getUrl();
    }
}