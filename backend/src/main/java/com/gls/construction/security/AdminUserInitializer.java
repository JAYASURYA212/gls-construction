package com.gls.construction.security;

import com.gls.construction.models.AdminUser;
import com.gls.construction.repositories.AdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {
    @Bean CommandLineRunner createAdmin(AdminUserRepository users, PasswordEncoder encoder,
        @Value("${ADMIN_USERNAME:}") String username, @Value("${ADMIN_PASSWORD:}") String password) {
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