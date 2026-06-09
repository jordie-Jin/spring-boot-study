package com.sesac.ai.backend.dto.response;

import com.sesac.ai.backend.domain.Role;
import com.sesac.ai.backend.domain.User;


 //사용자 응답 DTO
public record UserResponse(Long id, String username, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }
}
