package com.sesac.ai.backend.service;

import com.sesac.ai.backend.domain.Department;
import com.sesac.ai.backend.dto.DepartmentRequest;
import com.sesac.ai.backend.dto.DepartmentResponse;
import com.sesac.ai.backend.error.NotFoundException;
import com.sesac.ai.backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> list() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse get(Long id) {
        return DepartmentResponse.from(findDepartment(id));
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.name())
                .build();

        return DepartmentResponse.from(departmentRepository.save(department));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Department", id));
    }
}
