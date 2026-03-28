package com.khubeev.service;

import com.khubeev.dto.UserDto;
import com.khubeev.model.Role;
import com.khubeev.model.User;
import com.khubeev.repository.JpaUserRepository;
import com.khubeev.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class JpaUserService {

    private final JpaUserRepository jpaUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    public JpaUserService(JpaUserRepository jpaUserRepository,
                          PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository,
                          EmailService emailService) {
        this.jpaUserRepository = jpaUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional
    public UserDto createUser(String username, String rawPassword, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username can't be empty!");
        }
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password can't be empty!");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email can't be empty!");
        }
        if (jpaUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }
        if (jpaUserRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered!");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role 'ROLE_USER' not found in database"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setEnabled(false);
        user.setVerificationCode(UUID.randomUUID().toString());
        user.setRoles(List.of(userRole));

        User savedUser = jpaUserRepository.saveAndFlush(user);

        emailService.sendVerificationEmail(email, username, user.getVerificationCode());

        return convertToDto(savedUser);
    }

    @Transactional
    public void verifyUser(String verificationCode) {
        User user = jpaUserRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("Invalid verification code!"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account already verified!");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        jpaUserRepository.saveAndFlush(user);
    }

    @Transactional
    public UserDto updateUser(Long id, String username) {
        User user = jpaUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUsername(username);
        User updatedUser = jpaUserRepository.saveAndFlush(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        jpaUserRepository.deleteById(id);
        jpaUserRepository.flush();
    }
}