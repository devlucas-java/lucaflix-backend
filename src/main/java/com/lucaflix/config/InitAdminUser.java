//package com.lucaflix.config;
//
//import com.lucaflix.model.AdminPanel;
//import com.lucaflix.model.Anime;
//import com.lucaflix.model.User;
//import com.lucaflix.model.enums.Role;
//import com.lucaflix.repository.AnimeRepository;
//import com.lucaflix.repository.MovieRepository;
//import com.lucaflix.repository.SerieRepository;
//import com.lucaflix.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class InitAdminUser {
//
//    @Autowired
//    private SerieRepository serieRepository;
//    @Autowired
//    private MovieRepository movieRepository;
//    @Autowired
//    private AnimeRepository animeRepository;
////
//////    @Value("${admin.email}")
////    String envEmail = System.getenv("INIT_EMAIL");
////
////    private String adminEmail = envEmail;
////
//////    @Value("${admin.password}")
////    String envPassword = System.getenv("INIT_PASSWORD");
////    private String adminPassword = envPassword;
////
//////    @Value("${admin.firstname}")
////    String envFirstName = System.getenv("INIT_FIRST_NAME");
////    private String adminFirstName = envFirstName;
////
//////    @Value("${admin.lastname}")
////String envLastName = System.getenv("INIT_LAST_NAME");
////    private String adminLastName = envLastName;
////
//////    @Value("${admin.username}")
////String envUserName = System.getenv("INIT_USERNAME");
////
////    private String adminUsername = envUserName;
////
////    @Bean
////    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
////        return args -> {
////            // Avoid creating duplicate
////            if (userRepository.findByEmail(adminEmail).isPresent()) {
////                System.out.println("Admin user already exists.");
////                return;
////            }
////
////            if (adminPassword == null || adminPassword.trim().isEmpty()) {
////                System.out.println("Admin password not configured. Skipping admin creation.");
////                return;
////            }
////
////            User user = new User();
////            user.setLastName(adminLastName );
////            user.setFirstName(adminFirstName);
////            user.setUsername(adminUsername);
////            user.setEmail(adminEmail);
////            user.setPassword(passwordEncoder.encode(adminPassword));
////            user.setRole(Role.SUPER_ADMIN);
////
////            AdminPanel adminPanel = new AdminPanel();
////            adminPanel.setUser(user);
////            user.setAdminPanel(adminPanel);
////
////            userRepository.save(user);
////
////            System.out.println("Super Admin user created successfully!");
////        };
////    }
//
//
//
//
//    @Bean
//    CommandLineRunner deleteAllSeries(){
//    return args ->
//        serieRepository.deleteAll();
//    }
//
//    @Bean
//    CommandLineRunner deleteAllFilmes(){
//        return args ->
//                movieRepository.deleteAll();
//    }
//
//    @Bean
//    CommandLineRunner deleteAllAnimes(){
//        return args ->
//                animeRepository.deleteAll();
//    }
//}