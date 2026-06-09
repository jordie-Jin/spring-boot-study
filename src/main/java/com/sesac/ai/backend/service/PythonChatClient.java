package com.sesac.ai.backend.service;

import com.sesac.ai.backend.dto.request.ChatRequest;
import com.sesac.ai.backend.dto.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PythonChatClient {

    private final WebClient pythonWebClient;

    public Mono<ChatResponse> chat(String prompt) {
        return pythonWebClient.post()
                .uri("/chat")
                .bodyValue(new ChatRequest(prompt))
                .retrieve()
                .bodyToMono(ChatResponse.class);
    }
}
