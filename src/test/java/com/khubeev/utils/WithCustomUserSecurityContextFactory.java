package com.khubeev.utils;

import com.khubeev.model.Role;
import com.khubeev.model.User;
import com.khubeev.service.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = new User();
        user.setId(1L);
        user.setUsername(annotation.username());
        user.setEnabled(true);

        Role role = new Role();
        role.setName("ROLE_" + annotation.role());
        user.setRoles(List.of(role));

        CustomUserDetails principal = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, "password", principal.getAuthorities()
        );
        context.setAuthentication(auth);
        return context;
    }
}