package com.dby.ai.service;

import com.dby.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * AI 对话业务逻辑层
 * 通过 OpenAI REST API 实现智能对话、文本摘要和情感分析功能
 */
@Service
public class AiChatService {

    private static final String DEFAULT_SYSTEM_PROMPT =
            "你是 DBY 智能助手，一个专业、友好的 AI 助理。" +
            "请用清晰、准确的中文回答用户问题。" +
            "如果不确定某个问题的答案，请诚实告知用户。";

    private final WebClient openAiWebClient;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Value("${openai.max-tokens:2000}")
    private int maxTokens;

    @Value("${openai.temperature:0.7}")
    private double temperature;

    public AiChatService(WebClient openAiWebClient) {
        this.openAiWebClient = openAiWebClient;
    }

    /**
     * 发送聊天消息并获取 AI 回复
     */
    public ChatResponse chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();

        String systemPrompt = (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank())
                ? request.getSystemPrompt()
                : DEFAULT_SYSTEM_PROMPT;

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", request.getMessage())
                ),
                "max_tokens", maxTokens,
                "temperature", temperature
        );

        try {
            OpenAiResponse response = openAiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(OpenAiResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new BusinessException("AI 服务返回空响应");
            }

            String reply = response.getChoices().get(0).getMessage().getContent();
            long processingTimeMs = System.currentTimeMillis() - startTime;
            return new ChatResponse(reply, model, processingTimeMs);

        } catch (WebClientResponseException e) {
            throw new BusinessException(e.getStatusCode().value(),
                    "OpenAI API 请求失败: " + e.getResponseBodyAsString());
        }
    }

    /**
     * 文本摘要
     */
    public ChatResponse summarize(String text) {
        ChatRequest request = new ChatRequest();
        request.setSystemPrompt("你是一个文本摘要专家。请将用户提供的文本总结为简洁的要点，使用中文回复。");
        request.setMessage("请对以下文本进行摘要：\n\n" + text);
        return chat(request);
    }

    /**
     * 情感分析
     */
    public ChatResponse analyzeSentiment(String text) {
        ChatRequest request = new ChatRequest();
        request.setSystemPrompt(
                "你是一个情感分析专家。请分析用户提供文本的情感倾向，" +
                "判断为：正面、负面或中性，并给出置信度（0-100%）和原因。使用中文回复。"
        );
        request.setMessage("请分析以下文本的情感：\n\n" + text);
        return chat(request);
    }
}
