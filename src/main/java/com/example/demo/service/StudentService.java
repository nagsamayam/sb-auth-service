package com.example.demo.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Address;
import com.example.demo.entity.Student;
import com.example.demo.enums.StudentStatus;
import com.example.demo.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student save() {
        Address address = new Address();
        address.setHouseNo("3-3");
        address.setStreet("Neelipudi");
        address.setCity("Bantumilli");
        address.setState("Andhra Pradesh");
        address.setZipCode("521324");

        Student student = new Student();
        student.setFirstName("Nageswara Rao");
        student.setLastName("Samayam");
        student.setEmail("nag.samayam1@gmail.com");
        student.setCreatedAt(Instant.now());
        student.setAge(39);
        student.setStatus(StudentStatus.ACTIVE);
        student.setIsMonitor(false);
        student.setPercentage(BigDecimal.valueOf(99.99));

        student.setCurrentAddress(address);
        return studentRepository.save(student);
    }
}
