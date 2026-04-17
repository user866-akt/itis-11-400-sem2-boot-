package com.khubeev.service;

import com.khubeev.model.Role;
import com.khubeev.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private User user;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        user.setRoles(List.of(userRole));

        userDetails = new CustomUserDetails(user);
    }

    @Test
    void getAuthorities_ShouldReturnUserRoles() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void getAuthorities_WhenRolesNull_ShouldReturnEmptyList() {
        user.setRoles(null);
        userDetails = new CustomUserDetails(user);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).isEmpty();
    }

    @Test
    void getPassword_ShouldReturnUserPassword() {
        assertThat(userDetails.getPassword()).isEqualTo("password");
    }

    @Test
    void getUsername_ShouldReturnUsername() {
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
    }

    @Test
    void isEnabled_ShouldReturnUserEnabledStatus() {
        assertThat(userDetails.isEnabled()).isTrue();

        user.setEnabled(false);
        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void getUser_ShouldReturnUser() {
        assertThat(userDetails.getUser()).isEqualTo(user);
    }

    @Test
    void isAdmin_WhenUserHasAdminRole_ShouldReturnTrue() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        user.setRoles(List.of(adminRole));
        userDetails = new CustomUserDetails(user);

        assertThat(userDetails.isAdmin()).isTrue();
    }

    @Test
    void isAdmin_WhenUserHasNoAdminRole_ShouldReturnFalse() {
        assertThat(userDetails.isAdmin()).isFalse();
    }

    @Test
    void isAccountNonExpired_ShouldReturnTrue() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_ShouldReturnTrue() {
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_ShouldReturnTrue() {
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }
}