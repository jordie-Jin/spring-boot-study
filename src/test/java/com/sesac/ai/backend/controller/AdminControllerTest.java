package com.sesac.ai.backend.controller;

import com.sesac.ai.backend.domain.Role;
import com.sesac.ai.backend.domain.User;
import com.sesac.ai.backend.dto.request.RoleUpdateRequest;
import com.sesac.ai.backend.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    private AdminService adminService;
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        adminService = mock(AdminService.class);
        adminController = new AdminController(adminService);
    }

    @Test
    void listUsersUsesAdminService() {
        User user = user(1L, Role.USER);
        when(adminService.findAll()).thenReturn(List.of(user));

        List<Map<String, Object>> response = adminController.listUsers();

        assertThat(response).containsExactly(Map.of(
                "id", 1L,
                "username", "user@example.com",
                "role", "USER",
                "provider", "LOCAL"
        ));
        verify(adminService).findAll();
    }

    @Test
    void changeRoleUsesAdminService() {
        User admin = user(1L, Role.ADMIN);
        when(adminService.changeRole(1L, Role.ADMIN)).thenReturn(admin);

        Map<String, Object> response =
                adminController.changeRole(1L, new RoleUpdateRequest(Role.ADMIN));

        assertThat(response.get("role")).isEqualTo("ADMIN");
        verify(adminService).changeRole(1L, Role.ADMIN);
    }

    @Test
    void deleteUserUsesAdminService() {
        var response = adminController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(adminService).deleteUser(1L);
    }

    private User user(Long id, Role role) {
        return User.builder()
                .id(id)
                .username("user@example.com")
                .role(role)
                .provider("LOCAL")
                .build();
    }
}
