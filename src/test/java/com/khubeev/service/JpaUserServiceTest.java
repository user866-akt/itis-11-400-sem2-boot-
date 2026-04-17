package com.khubeev.service;

import com.khubeev.dto.UserDto;
import com.khubeev.model.Role;
import com.khubeev.model.User;
import com.khubeev.repository.JpaUserRepository;
import com.khubeev.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserServiceTest {

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private JpaUserService jpaUserService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(false);
        testUser.setVerificationCode("test-code-123");
        testUser.setRoles(List.of(userRole));
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        when(jpaUserRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = jpaUserService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = jpaUserService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldReturnNull() {
        when(jpaUserRepository.findById(99L)).thenReturn(Optional.empty());

        UserDto result = jpaUserService.findById(99L);

        assertThat(result).isNull();
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        when(jpaUserRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDto result = jpaUserService.findByUsername("testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jpaUserRepository.saveAndFlush(any(User.class))).thenReturn(testUser);
        doNothing().when(emailService).sendVerificationEmail(any(), any(), any());

        UserDto result = jpaUserService.createUser("newuser", "password123", "new@example.com");

        assertThat(result).isNotNull();
        verify(jpaUserRepository).saveAndFlush(any(User.class));
        verify(emailService).sendVerificationEmail(any(), any(), any());
    }

    @Test
    void createUser_WithEmptyUsername_ShouldThrowException() {
        assertThatThrownBy(() -> jpaUserService.createUser("", "pass", "email@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username can't be empty!");
    }

    @Test
    void createUser_WithEmptyPassword_ShouldThrowException() {
        assertThatThrownBy(() -> jpaUserService.createUser("user", "", "email@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password can't be empty!");
    }

    @Test
    void createUser_WithEmptyEmail_ShouldThrowException() {
        assertThatThrownBy(() -> jpaUserService.createUser("user", "pass", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email can't be empty!");
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        when(jpaUserRepository.findByUsername("existing")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> jpaUserService.createUser("existing", "pass", "email@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already exists!");
    }

    @Test
    void verifyUser_WithValidCode_ShouldEnableUser() {
        when(jpaUserRepository.findByVerificationCode("valid-code")).thenReturn(Optional.of(testUser));
        when(jpaUserRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        jpaUserService.verifyUser("valid-code");

        assertThat(testUser.isEnabled()).isTrue();
        assertThat(testUser.getVerificationCode()).isNull();
        verify(jpaUserRepository).saveAndFlush(testUser);
    }

    @Test
    void verifyUser_WithInvalidCode_ShouldThrowException() {
        when(jpaUserRepository.findByVerificationCode("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jpaUserService.verifyUser("invalid"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid verification code!");
    }

    @Test
    void verifyUser_WhenAlreadyVerified_ShouldThrowException() {
        testUser.setEnabled(true);
        when(jpaUserRepository.findByVerificationCode("code")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> jpaUserService.verifyUser("code"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account already verified!");
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaUserRepository.saveAndFlush(any(User.class))).thenReturn(testUser);

        UserDto result = jpaUserService.updateUser(1L, "updatedname");

        assertThat(result).isNotNull();
        assertThat(testUser.getUsername()).isEqualTo("updatedname");
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        doNothing().when(jpaUserRepository).deleteById(1L);
        doNothing().when(jpaUserRepository).flush();

        jpaUserService.deleteUser(1L);

        verify(jpaUserRepository).deleteById(1L);
        verify(jpaUserRepository).flush();
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        when(jpaUserRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> jpaUserService.createUser("newuser", "pass", "existing@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already registered!");
    }

    @Test
    void createUser_WhenRoleNotFound_ShouldThrowException() {
        when(jpaUserRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(jpaUserRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jpaUserService.createUser("newuser", "pass", "new@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Default role 'ROLE_USER' not found in database");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnNull() {
        when(jpaUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserDto result = jpaUserService.findByUsername("unknown");

        assertThat(result).isNull();
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        when(jpaUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jpaUserService.updateUser(99L, "newname"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 99");
    }
}