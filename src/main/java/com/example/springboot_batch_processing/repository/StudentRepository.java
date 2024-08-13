package com.example.springboot_batch_processing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springboot_batch_processing.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}