package com.khubeev.service;

import com.khubeev.dto.UserDto;
import com.khubeev.model.User;
import com.khubeev.repository.HibernateUserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HibernateUserService {

    private final HibernateUserRepository hibernateUserRepository;

    public HibernateUserService(HibernateUserRepository hibernateUserRepository) {
        this.hibernateUserRepository = hibernateUserRepository;
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }

    @Transactional
    public List<UserDto> findAll() {
        return hibernateUserRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public UserDto findById(Long id) {
        User user = hibernateUserRepository.findById(id);
        if (user != null) {
            return convertToDto(user);
        } else {
            return null;
        }
    }

    @Transactional
    public UserDto createUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username can't be empty!");
        }
        User user = new User();
        user.setUsername(username);
        User savedUser = hibernateUserRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, String username) {
        User user = hibernateUserRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        user.setUsername(username);
        User updatedUser = hibernateUserRepository.update(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        hibernateUserRepository.delete(id);
    }
}
