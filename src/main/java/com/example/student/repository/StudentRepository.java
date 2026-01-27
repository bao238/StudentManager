package com.example.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.student.model.Student;

import java.util.List;

public interface StudentRepository
        extends JpaRepository<Student, Integer> {
    
    // Tìm kiếm sinh viên theo tên (không phân biệt hoa thường)
    List<Student> findByNameContainingIgnoreCase(String name);
}
