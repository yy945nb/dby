package com.dby.ai.controller;

import com.dby.ai.service.AiChatService;
import com.dby.ai.service.ChatRequest;
import com.dby.ai.service.ChatResponse;
import com.dby.common.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * AI REST API 控制器
 */
@RestController
@RequestMapping("/api/ai")
@Validated
public class AiController {

    private final AiChatService aiChatService;

    public AiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * 智能对话
     */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = aiChatService.chat(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 文本摘要
     */
    @PostMapping("/summarize")
    public ResponseEntity<ApiResponse<ChatResponse>> summarize(
            @RequestParam @NotBlank(message = "文本内容不能为空") String text) {
        ChatResponse response = aiChatService.summarize(text);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 情感分析
     */
    @PostMapping("/sentiment")
    public ResponseEntity<ApiResponse<ChatResponse>> analyzeSentiment(
            @RequestParam @NotBlank(message = "文本内容不能为空") String text) {
        ChatResponse response = aiChatService.analyzeSentiment(text);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 健康检查（不调用 AI，用于快速探活）
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("AI 服务运行正常"));
    }
}
