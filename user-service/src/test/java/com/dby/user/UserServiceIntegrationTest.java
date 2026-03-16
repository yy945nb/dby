package com.dby.user;

import com.dby.user.model.RegisterRequest;
import com.dby.user.model.User;
import com.dby.user.model.UserResponse;
import com.dby.user.repository.UserRepository;
import com.dby.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户服务集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterNewUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@example.com");
        request.setPassword("password123");
        request.setNickname("Test User");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("testuser@example.com"));
    }

    @Test
    void shouldRejectDuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicateuser");
        request.setEmail("dup1@example.com");
        request.setPassword("password123");
        userService.register(request);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setUsername("duplicateuser");
        duplicate.setEmail("dup2@example.com");
        duplicate.setPassword("password123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("validuser");
        request.setEmail("not-an-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserById() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("getbyiduser");
        request.setEmail("getbyid@example.com");
        request.setPassword("password123");
        UserResponse created = userService.register(request);

        mockMvc.perform(get("/api/users/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(created.getId()))
                .andExpect(jsonPath("$.data.username").value("getbyiduser"));
    }

    @Test
    void shouldReturnNotFoundForUnknownUser() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldEncodePassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("secureuser");
        request.setEmail("secure@example.com");
        request.setPassword("plaintext");
        UserResponse created = userService.register(request);
        assertThat(created).isNotNull();
        assertThat(created.getUsername()).isEqualTo("secureuser");

        // Verify the stored password is BCrypt encoded (starts with $2) and differs from plaintext
        User rawUser = userRepository.findByUsername("secureuser").orElseThrow();
        assertThat(rawUser.getPassword()).isNotEqualTo("plaintext");
        assertThat(rawUser.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("plaintext", rawUser.getPassword())).isTrue();
    }
}
