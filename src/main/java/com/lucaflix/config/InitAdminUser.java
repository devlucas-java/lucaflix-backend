//package com.lucaflix.config;
//
//import com.lucaflix.model.AdminPanel;
//import com.lucaflix.model.User;
//import com.lucaflix.model.enums.Role;
//import com.lucaflix.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class InitAdminUser {
//
//    @Value("${admin.email}")
//    private String adminEmail;
//
//    @Value("${admin.password}")
//    private String adminPassword;
//
//    @Value("${admin.firstname}")
//    private String adminFirstName;
//
//    @Value("${admin.lastname}")
//    private String adminLastName;
//
//    @Value("${admin.username}")
//    private String adminUsername;
//
//    @Bean
//    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        return args -> {
//            // Avoid creating duplicate
//            if (userRepository.findByEmail(adminEmail).isPresent()) {
//                System.out.println("Admin user already exists.");
//                return;
//            }
//
//            if (adminPassword == null || adminPassword.trim().isEmpty()) {
//                System.out.println("Admin password not configured. Skipping admin creation.");
//                return;
//            }
//
//            User user = new User();
//            user.setLastName(adminLastName );
//            user.setFirstName(adminFirstName);
//            user.setUsername(adminUsername);
//            user.setEmail(adminEmail);
//            user.setPassword(passwordEncoder.encode(adminPassword));
//            user.setRole(Role.SUPER_ADMIN);
//
//            AdminPanel adminPanel = new AdminPanel();
//            adminPanel.setUser(user);
//            user.setAdminPanel(adminPanel);
//
//            userRepository.save(user);
//
//            System.out.println("Super Admin user created successfully!");
//        };
//    }
//}