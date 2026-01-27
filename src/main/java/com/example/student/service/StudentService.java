package com.example.student.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.student.model.Student;
import com.example.student.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> getAll() {
        return repository.findAll();
    }

    public Student getById(int id) {
        return repository.findById(id).orElse(null);
    }

    public Student save(Student student) {
        return repository.save(student);
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

    public List<Student> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }
}
