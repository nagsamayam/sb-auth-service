package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;

import com.example.demo.converters.BooleanToStringConverter;
import com.example.demo.enums.StudentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Transient
    private String displayName;

    @Lob
    private String profileDescription;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(length = 3)
    private Boolean isMonitor;

    private int age;

    @ElementCollection
    @CollectionTable(name = "student_address", joinColumns = @JoinColumn(name = "student_id"))
    private Set<Address> permanentAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "houseNo", column = @Column(name = "current_house_no")),
        @AttributeOverride(name = "street", column = @Column(name = "current_street")),
        @AttributeOverride(name = "city", column = @Column(name = "current_city")),
        @AttributeOverride(name = "state", column = @Column(name = "current_state")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "current_zip_code"))
    })
    private Address currentAddress;

    private LocalDate dateOfBirth;
}
