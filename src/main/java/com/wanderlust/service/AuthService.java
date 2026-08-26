package com.wanderlust.service;

import com.wanderlust.dto.AuthResponse;
import com.wanderlust.dto.LoginRequest;
import com.wanderlust.dto.RegisterRequest;
import com.wanderlust.entity.Role;
import com.wanderlust.entity.User;
import com.wanderlust.exception.ResourceNotFoundException;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(
	        UserRepository userRepository,
	        PasswordEncoder passwordEncoder,
	        JwtService jwtService) {

	    this.userRepository = userRepository;
	    this.passwordEncoder = passwordEncoder;
	    this.jwtService = jwtService;
	}

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Hash password before storing
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setPhone(request.getPhone());

        // Every public registration starts as USER
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }
    
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }
}