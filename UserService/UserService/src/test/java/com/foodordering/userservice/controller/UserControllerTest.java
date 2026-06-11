package com.foodordering.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.userservice.dto.RegisterRequest;
import com.foodordering.userservice.entity.Role;
import com.foodordering.userservice.entity.User;
import com.foodordering.userservice.repository.UserRepository;
import com.foodordering.userservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired JwtService jwtService;
    @Autowired PasswordEncoder passwordEncoder;

    private String customerToken;
    private String adminToken;
    private User customerUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        customerUser = userRepository.save(User.builder()
                .username("john")
                .email("john@example.com")
                .password(passwordEncoder.encode("secret123"))
                .role(Role.CUSTOMER)
                .build());

        userRepository.save(User.builder()
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("secret123"))
                .role(Role.ADMIN)
                .build());

        UserDetails customerDetails = new org.springframework.security.core.userdetails.User(
                "john", "secret123", List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        UserDetails adminDetails = new org.springframework.security.core.userdetails.User(
                "admin", "secret123", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        customerToken = "Bearer " + jwtService.generateToken(customerDetails);
        adminToken = "Bearer " + jwtService.generateToken(adminDetails);
    }

    @Test
    void getMyProfile_returnsProfile() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void getMyProfile_returns401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        mockMvc.perform(get("/users/" + customerUser.getId())
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    void getUserById_returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/users/99999")
                        .header("Authorization", customerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void userExists_returnsTrueWhenFound() throws Exception {
        mockMvc.perform(get("/users/" + customerUser.getId() + "/exists")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void userExists_returnsFalseWhenNotFound() throws Exception {
        mockMvc.perform(get("/users/99999/exists")
                        .header("Authorization", customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    void getAllUsers_returnsListForAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").exists());
    }

    @Test
    void getAllUsers_returns403ForCustomer() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", customerToken))
                .andExpect(status().isForbidden());
    }
}
