package com.sesac.ai.backend.repository;

import com.sesac.ai.backend.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
