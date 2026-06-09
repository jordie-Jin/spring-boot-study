package com.sesac.ai.backend.service;

import com.sesac.ai.backend.domain.Department;
import com.sesac.ai.backend.domain.Employee;
import com.sesac.ai.backend.dto.request.EmployeeRequest;
import com.sesac.ai.backend.dto.response.EmployeeResponse;
import com.sesac.ai.backend.error.NotFoundException;
import com.sesac.ai.backend.repository.DepartmentRepository;
import com.sesac.ai.backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> list() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(Long id) {
        return EmployeeResponse.from(findEmployee(id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> listByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw NotFoundException.of("Department", departmentId);
        }

        return employeeRepository.findByDepartmentId(departmentId).stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        Department department = findDepartment(request.departmentId());
        Employee employee = Employee.builder()
                .department(department)
                .position(request.position())
                .email(request.email())
                .build();

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        Department department = findDepartment(request.departmentId());

        employee.setDepartment(department);
        employee.setPosition(request.position());
        employee.setEmail(request.email());

        return EmployeeResponse.from(employee);
    }

    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw NotFoundException.of("Employee", id);
        }

        employeeRepository.deleteById(id);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Employee", id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Department", id));
    }
}
