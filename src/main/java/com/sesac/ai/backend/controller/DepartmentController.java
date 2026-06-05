package com.sesac.ai.backend.controller;

import com.sesac.ai.backend.dto.DepartmentRequest;
import com.sesac.ai.backend.dto.DepartmentResponse;
import com.sesac.ai.backend.dto.EmployeeResponse;
import com.sesac.ai.backend.service.DepartmentService;
import com.sesac.ai.backend.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @GetMapping
    public List<DepartmentResponse> list() {
        return departmentService.list();
    }

    @GetMapping("/{id}")
    public DepartmentResponse get(@PathVariable Long id) {
        return departmentService.get(id);
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse created = departmentService.create(request);
        return ResponseEntity.created(URI.create("/departments/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}/employees")
    public List<EmployeeResponse> listEmployees(@PathVariable Long id) {
        return employeeService.listByDepartment(id);
    }
}
