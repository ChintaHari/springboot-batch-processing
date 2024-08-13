package com.example.springboot_batch_processing.config;

import org.springframework.batch.item.ItemProcessor;

import com.example.springboot_batch_processing.entity.Student;

public class StudentProcessor implements ItemProcessor<Student,Student> {

    @Override
    public Student process(Student student) {
        return student;
    }
}