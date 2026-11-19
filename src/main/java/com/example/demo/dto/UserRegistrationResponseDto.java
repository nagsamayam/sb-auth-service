package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class UserRegistrationResponseDto {
    private Long id;
    private String username;
}
