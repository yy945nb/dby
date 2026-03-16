package com.dby.ai;

import com.dby.ai.service.AiChatService;
import com.dby.ai.service.ChatRequest;
import com.dby.ai.service.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AI 服务集成测试（使用 Mock 替代真实 OpenAI 调用）
 */
@SpringBootTest
@AutoConfigureMockMvc
class AiServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiChatService aiChatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthEndpointShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/ai/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("AI 服务运行正常"));
    }

    @Test
    void chatShouldReturnAiReply() throws Exception {
        ChatResponse mockResponse = new ChatResponse("你好！我是 DBY 智能助手，有什么可以帮您？", "MockModel", 150L);
        when(aiChatService.chat(any(ChatRequest.class))).thenReturn(mockResponse);

        ChatRequest request = new ChatRequest("你好");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reply").value("你好！我是 DBY 智能助手，有什么可以帮您？"));
    }

    @Test
    void chatShouldRejectEmptyMessage() throws Exception {
        ChatRequest request = new ChatRequest("");

        mockMvc.perform(post("/api/ai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void summarizeShouldReturnSummary() throws Exception {
        ChatResponse mockResponse = new ChatResponse("文本摘要：这是一段测试文本。", "MockModel", 200L);
        when(aiChatService.summarize(any(String.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/ai/summarize")
                .param("text", "这是一段需要摘要的测试文本，内容比较长。"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reply").exists());
    }

    @Test
    void sentimentShouldReturnAnalysis() throws Exception {
        ChatResponse mockResponse = new ChatResponse("情感分析结果：正面（90%）", "MockModel", 180L);
        when(aiChatService.analyzeSentiment(any(String.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/ai/sentiment")
                .param("text", "今天天气真好，心情非常愉快！"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reply").exists());
    }
}
