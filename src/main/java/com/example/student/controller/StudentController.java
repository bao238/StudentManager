package com.example.student.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.student.model.Student;
import com.example.student.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // View: Hiển thị trang index.html
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Yêu cầu 5: API lấy danh sách sinh viên - Get All
    @GetMapping("/api/students")
    @ResponseBody
    public ResponseEntity<List<Student>> getAllStudents() {
        try {
            List<Student> students = service.getAll();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // Yêu cầu 4: API lấy sinh viên theo ID
    @GetMapping("/api/students/{id}")
    @ResponseBody
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        try {
            Student student = service.getById(id);
            if (student != null) {
                return ResponseEntity.ok(student);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Yêu cầu 3: API tìm kiếm sinh viên theo tên
    @GetMapping("/api/students/search")
    @ResponseBody
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String name) {
        try {
            List<Student> students = service.searchByName(name);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // API tìm kiếm sinh viên theo ID
    @GetMapping("/api/students/search/id")
    @ResponseBody
    public ResponseEntity<List<Student>> searchStudentsById(@RequestParam int id) {
        try {
            Student student = service.getById(id);
            List<Student> result = new ArrayList<>();
            if (student != null) {
                result.add(student);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // Yêu cầu 1: API thêm sinh viên
    @PostMapping("/api/students")
    @ResponseBody
    public ResponseEntity<?> addStudent(@RequestBody Map<String, Object> data) {
        try {
            // Lấy dữ liệu từ Map để tránh vấn đề với id
            String name = data.get("name") != null ? data.get("name").toString().trim() : null;
            String email = data.get("email") != null ? data.get("email").toString().trim() : null;
            Object ageObj = data.get("age");
            int age = 0;
            
            if (ageObj instanceof Number) {
                age = ((Number) ageObj).intValue();
            } else if (ageObj != null) {
                age = Integer.parseInt(ageObj.toString());
            }
            
            // Validate
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body("Tên không được để trống");
            }
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email không được để trống");
            }
            if (age <= 0) {
                return ResponseEntity.badRequest().body("Tuổi phải lớn hơn 0");
            }
            
            // Tạo student mới - KHÔNG set id để JPA tự generate
            Student newStudent = new Student();
            // KHÔNG gọi setId() - để id = null để JPA biết đây là insert mới
            newStudent.setName(name);
            newStudent.setAge(age);
            newStudent.setEmail(email);
            
            System.out.println("Creating new student - ID before save: " + newStudent.getId());
            Student savedStudent = service.save(newStudent);
            System.out.println("Saved student - ID after save: " + savedStudent.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Tuổi không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Lỗi: " + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += " - " + e.getCause().getMessage();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }

    // Yêu cầu 6: API cập nhật sinh viên
    @PostMapping("/api/students/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStudent(@PathVariable int id, @RequestBody Map<String, Object> data) {
        try {
            Student existingStudent = service.getById(id);
            if (existingStudent == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Lấy dữ liệu từ Map
            String name = data.get("name") != null ? data.get("name").toString().trim() : null;
            String email = data.get("email") != null ? data.get("email").toString().trim() : null;
            Object ageObj = data.get("age");
            int age = 0;
            
            if (ageObj instanceof Number) {
                age = ((Number) ageObj).intValue();
            } else if (ageObj != null) {
                age = Integer.parseInt(ageObj.toString());
            }
            
            // Validate
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body("Tên không được để trống");
            }
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email không được để trống");
            }
            if (age <= 0) {
                return ResponseEntity.badRequest().body("Tuổi phải lớn hơn 0");
            }
            
            existingStudent.setName(name);
            existingStudent.setAge(age);
            existingStudent.setEmail(email);
            
            Student updatedStudent = service.save(existingStudent);
            return ResponseEntity.ok(updatedStudent);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Tuổi không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: " + e.getMessage());
        }
    }

    // Yêu cầu 2: API xóa sinh viên
    @PostMapping("/api/students/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteStudent(@PathVariable int id) {
        try {
            Student student = service.getById(id);
            if (student == null) {
                return ResponseEntity.notFound().build();
            }
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: " + e.getMessage());
        }
    }
}
