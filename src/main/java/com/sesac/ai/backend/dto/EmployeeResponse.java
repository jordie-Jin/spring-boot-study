package com.sesac.ai.backend.dto;

import com.sesac.ai.backend.domain.Employee;

public record EmployeeResponse(
        Long id,
        Long departmentId,
        String departmentName,
        String position,
        String email
) {

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getDepartment().getId(),
                employee.getDepartment().getName(),
                employee.getPosition(),
                employee.getEmail()
        );
    }
}
