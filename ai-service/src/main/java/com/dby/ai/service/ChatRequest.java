package com.dby.ai.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * AI 聊天请求 DTO
 */
public class ChatRequest {

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 4000, message = "消息长度不能超过 4000 个字符")
    private String message;

    private String systemPrompt;

    public ChatRequest() {}

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
}
