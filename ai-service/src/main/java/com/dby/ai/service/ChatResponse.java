package com.dby.ai.service;

/**
 * AI 聊天响应 DTO
 */
public class ChatResponse {

    private String reply;
    private String model;
    private long processingTimeMs;

    public ChatResponse() {}

    public ChatResponse(String reply, String model, long processingTimeMs) {
        this.reply = reply;
        this.model = model;
        this.processingTimeMs = processingTimeMs;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}
