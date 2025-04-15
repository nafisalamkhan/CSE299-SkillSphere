package com.cse299.skillSphere.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Controller
@RequestMapping("/user/courses")
public class ChatBotController {

    private final ChatClient chatClient;

    public ChatBotController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chatbot")
    public String chatbot() {
        return "chatbot";
    }

    @GetMapping("/stream")
    public String stream() {
        return "stream";
    }

    @PostMapping("/chat")
    @ResponseBody
    public String chat(@RequestParam String message) {
        System.out.println("Chat endpoint hit with message: " + message);
        try {
            String response = chatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            System.out.println("Response: " + response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing your request: " + e.getMessage();
        }
    }

    @GetMapping(value = "/stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> streamChat(@RequestParam String message) {
        System.out.println("Stream chat endpoint hit with message: " + message);
        try {
            return chatClient.prompt()
                    .user(message)
                    .stream()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            return Flux.error(e);
        }
    }
}