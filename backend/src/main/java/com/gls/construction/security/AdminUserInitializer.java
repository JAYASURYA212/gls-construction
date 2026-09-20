package com.gls.construction.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gls.construction.models.AdminUser;
import com.gls.construction.repositories.AdminUserRepository;

@Configuration
public class AdminUserInitializer {
    @Bean CommandLineRunner createAdmin(AdminUserRepository users, PasswordEncoder encoder,
        @Value("${ADMIN_USERNAME:GLS_Builders}") String username,
        @Value("${ADMIN_PASSWORD:GLS@2026}") String password) {
        return args -> {
            if (!username.isBlank() && !password.isBlank()) {
                AdminUser user = users.findByUsername(username).orElseGet(AdminUser::new);
                user.setUsername(username);
                user.setPasswordHash(encoder.encode(password));
                users.save(user);
            }
        };
    }
}