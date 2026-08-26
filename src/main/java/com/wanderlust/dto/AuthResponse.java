package com.wanderlust.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String token;
}