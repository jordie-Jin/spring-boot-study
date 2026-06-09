package com.sesac.ai.backend.controller;

import com.sesac.ai.backend.domain.ChatLog;
import com.sesac.ai.backend.domain.Role;
import com.sesac.ai.backend.domain.User;
import com.sesac.ai.backend.dto.request.ChatLogRequest;
import com.sesac.ai.backend.service.ChatLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatLogControllerTest {

    private ChatLogService chatLogService;
    private ChatLogController chatLogController;
    private UserDetails principal;

    @BeforeEach
    void setUp() {
        chatLogService = mock(ChatLogService.class);
        chatLogController = new ChatLogController(chatLogService);
        principal = org.springframework.security.core.userdetails.User
                .withUsername("alice")
                .password("unused")
                .roles("USER")
                .build();
    }

    @Test
    void listUsesAuthenticatedUsername() {
        when(chatLogService.findByUsername("alice")).thenReturn(List.of());

        assertThat(chatLogController.list(principal)).isEmpty();

        verify(chatLogService).findByUsername("alice");
    }

    @Test
    void createUsesAuthenticatedUsername() {
        ChatLog saved = ChatLog.builder()
                .id(10L)
                .user(User.builder()
                        .id(2L)
                        .username("alice")
                        .role(Role.USER)
                        .build())
                .prompt("hello")
                .response("world")
                .build();
        when(chatLogService.save("alice", "hello", "world")).thenReturn(saved);

        var response = chatLogController.create(
                principal,
                new ChatLogRequest("hello", "world"));

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation()).hasPath("/chat-logs/10");
        verify(chatLogService).save("alice", "hello", "world");
    }
}
