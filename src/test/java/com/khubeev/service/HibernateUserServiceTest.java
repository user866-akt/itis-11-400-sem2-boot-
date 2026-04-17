package com.khubeev.service;

import com.khubeev.dto.UserDto;
import com.khubeev.model.User;
import com.khubeev.repository.HibernateUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HibernateUserServiceTest {

    @Mock
    private HibernateUserRepository hibernateUserRepository;

    @InjectMocks
    private HibernateUserService hibernateUserService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        when(hibernateUserRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = hibernateUserService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        when(hibernateUserRepository.findById(1L)).thenReturn(testUser);

        UserDto result = hibernateUserService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldReturnNull() {
        when(hibernateUserRepository.findById(99L)).thenReturn(null);

        UserDto result = hibernateUserService.findById(99L);

        assertThat(result).isNull();
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        when(hibernateUserRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = hibernateUserService.createUser("newuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(hibernateUserRepository).save(any(User.class));
    }

    @Test
    void createUser_WithEmptyUsername_ShouldThrowException() {
        assertThatThrownBy(() -> hibernateUserService.createUser(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username can't be empty!");

        assertThatThrownBy(() -> hibernateUserService.createUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username can't be empty!");
    }

    @Test
    void createUser_WithWhitespaceUsername_ShouldThrowException() {
        assertThatThrownBy(() -> hibernateUserService.createUser("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username can't be empty!");
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        when(hibernateUserRepository.findById(1L)).thenReturn(testUser);
        when(hibernateUserRepository.update(any(User.class))).thenReturn(testUser);

        UserDto result = hibernateUserService.updateUser(1L, "updatedname");

        assertThat(result).isNotNull();
        assertThat(testUser.getUsername()).isEqualTo("updatedname");
        verify(hibernateUserRepository).update(testUser);
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        when(hibernateUserRepository.findById(99L)).thenReturn(null);

        assertThatThrownBy(() -> hibernateUserService.updateUser(99L, "newname"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 99");
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        doNothing().when(hibernateUserRepository).delete(1L);

        hibernateUserService.deleteUser(1L);

        verify(hibernateUserRepository).delete(1L);
    }
}