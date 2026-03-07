package com.khubeev.service;

import com.khubeev.dto.UserDto;
import com.khubeev.model.User;
import com.khubeev.repository.JpaUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "jpaTransactionManager")
public class JpaUserService {

    private final JpaUserRepository jpaUserRepository;

    public JpaUserService(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }

    @Transactional(value = "jpaTransactionManager", readOnly = true)
    public List<UserDto> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(value = "jpaTransactionManager", readOnly = true)
    public UserDto findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional(value = "jpaTransactionManager", readOnly = true)
    public UserDto findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional(value = "jpaTransactionManager")
    public UserDto createUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username can't be empty!");
        }
        User user = new User();
        user.setUsername(username);
        User savedUser = jpaUserRepository.saveAndFlush(user);
        return convertToDto(savedUser);
    }

    @Transactional(value = "jpaTransactionManager")
    public UserDto updateUser(Long id, String username) {
        User user = jpaUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUsername(username);
        User updatedUser = jpaUserRepository.saveAndFlush(user);

        return convertToDto(updatedUser);
    }

    @Transactional(value = "jpaTransactionManager")
    public void deleteUser(Long id) {
        jpaUserRepository.deleteById(id);
        jpaUserRepository.flush();
    }
}