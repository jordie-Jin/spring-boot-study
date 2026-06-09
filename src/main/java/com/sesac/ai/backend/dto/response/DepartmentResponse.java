package com.sesac.ai.backend.dto.response;

import com.sesac.ai.backend.domain.Department;

public record DepartmentResponse(Long id, String name) {

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getName());
    }
}
