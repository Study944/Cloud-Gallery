package com.cloudgallery.manager.ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;

public class ImageDescriptionManager {

    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static String apiKey = "你的apikey";

    public static String callApiWithImage(String imageUrl, String prompt)
            throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "glm-4v-plus-0111");
        requestBody.put("stream", false);

        ArrayNode messagesArray = objectMapper.createArrayNode();
        ObjectNode userMessage = objectMapper.createObjectNode();
        userMessage.put("role", "user");

        ArrayNode contentArray = objectMapper.createArrayNode();

        // Image URL part
        ObjectNode imageUrlContent = objectMapper.createObjectNode();
        imageUrlContent.put("type", "image_url");
        ObjectNode imageUrlObject = objectMapper.createObjectNode();
        imageUrlObject.put("url", imageUrl);
        imageUrlContent.set("image_url", imageUrlObject);
        contentArray.add(imageUrlContent);

        // Text part
        ObjectNode textContent = objectMapper.createObjectNode();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        contentArray.add(textContent);

        userMessage.set("content", contentArray);
        messagesArray.add(userMessage);

        requestBody.set("messages", messagesArray);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode responseBody = objectMapper.readTree(response.body());
        return responseBody.at("/choices/0/message/content").asText();
    }
}