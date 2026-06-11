package com.foodordering.userservice.service;

import com.foodordering.userservice.dto.UpdateProfileRequest;
import com.foodordering.userservice.dto.UserProfileResponse;
import com.foodordering.userservice.entity.Role;
import com.foodordering.userservice.entity.User;
import com.foodordering.userservice.exception.UserNotFoundException;
import com.foodordering.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .password("hashed")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    void getProfile_success() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserProfileResponse response = userService.getProfile("john");

        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void getProfile_throwsWhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileResponse response = userService.getUserById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
    }

    @Test
    void getUserById_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void userExists_returnsTrueWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertThat(userService.userExists(1L)).isTrue();
    }

    @Test
    void userExists_returnsFalseWhenNotExists() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThat(userService.userExists(99L)).isFalse();
    }

    @Test
    void updateProfile_updatesEmail() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("newemail@example.com");

        UserProfileResponse response = userService.updateProfile("john", request);

        assertThat(response.getEmail()).isEqualTo("newemail@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_updatesPassword() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("newhashed");
        when(userRepository.save(any())).thenReturn(user);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setPassword("newpassword");

        userService.updateProfile("john", request);

        assertThat(user.getPassword()).isEqualTo("newhashed");
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserProfileResponse> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john");
    }
}
