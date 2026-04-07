package com.lucaflix.service.utils;

import com.lucaflix.model.User;
import com.lucaflix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class GenerateUsernames {

    private final UserRepository userRepository;
    private final Random random = new Random();

    public String generateRandomUsername(User user) {
        String baseUsername = user.getFirstName().toLowerCase() + "-" + user.getLastName().toLowerCase();
        String username = baseUsername;

        while (userRepository.existsByUsername(username)) {
            int randomNumber = 1 + random.nextInt(999); // 1 a 999
            username = baseUsername + "-" + randomNumber;
        }

        return username;
    }
}