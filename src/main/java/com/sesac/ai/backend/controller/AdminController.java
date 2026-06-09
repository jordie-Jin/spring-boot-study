package com.sesac.ai.backend.controller;

import com.sesac.ai.backend.domain.User;
import com.sesac.ai.backend.dto.request.RoleUpdateRequest;
import com.sesac.ai.backend.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 관리자 전용 사용자 관리 라우트 (Day 5 역할 권한 시연).
 *
 * SecurityConfig의 requestMatchers("/admin/**").hasRole("ADMIN")와
 * 메서드 단의 @PreAuthorize("hasRole('ADMIN')") 양쪽으로 이중 보호합니다.
 *
 * 시드 계정: admin / admin1234 (DataInitializer, dev 프로파일 한정)
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<Map<String, Object>> listUsers() {
        return adminService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{id}/role")
    public Map<String, Object> changeRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        return toResponse(adminService.changeRole(id, request.role()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toResponse(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "provider", user.getProvider()
        );
    }
}
