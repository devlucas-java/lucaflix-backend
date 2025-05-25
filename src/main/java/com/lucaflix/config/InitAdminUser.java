package com.lucaflix.config;

import com.chicahot.api.model.AdminProfile;
import com.chicahot.api.model.User;
import com.chicahot.api.model.enums.UserRole;
import com.chicahot.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InitAdminUser {

    @Value("${admin.email:lucas@chicahot.com}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.firstname:Lucas}")
    private String adminFirstName;

    @Value("${admin.lastname:Macedo}")
    private String adminLastName;

    @Value("${admin.username:lucas_admin}")
    private String adminUsername;

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Avoid creating duplicate
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                System.out.println("Admin user already exists.");
                return;
            }

            if (adminPassword == null || adminPassword.trim().isEmpty()) {
                System.out.println("Admin password not configured. Skipping admin creation.");
                return;
            }

            User user = new User();
            user.setEmailVerified(true);
            user.setFirstName(adminFirstName);
            user.setLastName(adminLastName);
            user.setMyUserName(adminUsername);
            user.setEmail(adminEmail);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole(UserRole.SUPER_ADMIN);

            AdminProfile adminProfile = new AdminProfile();
            adminProfile.setUser(user);
            user.setAdminProfile(adminProfile);

            userRepository.save(user);

            System.out.println("Super Admin user created successfully!");
        };
    }
}