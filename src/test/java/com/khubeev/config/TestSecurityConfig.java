package com.khubeev.config;

import com.khubeev.model.Role;
import com.khubeev.model.User;
import com.khubeev.service.CustomUserDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
        user.setRoles(List.of(userRole));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setEnabled(true);

        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");
        admin.setRoles(List.of(adminRole));

        CustomUserDetails adminDetails = new CustomUserDetails(admin);

        return new InMemoryUserDetailsManager(userDetails, adminDetails);
    }
}