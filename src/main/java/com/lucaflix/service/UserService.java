package com.lucaflix.service;

import com.lucaflix.dto.mapper.UserMapper;
import com.lucaflix.dto.request.user.UpdateUserDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.model.User;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MinhaListaRepository;
import com.lucaflix.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final LikeRepository likeRepository;
    private final UserMapper userMapper;

    public UserDTO getMe(User userRequest) {

        User user = userRepository.findById(userRequest.getId()).
                orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserDTO(user);
    }

    @Transactional
    public UserDTO updateMe(User userRequest, UpdateUserDTO request) {

        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            userRequest.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            userRequest.setLastName(request.getLastName().trim());
        }

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            String newUsername = request.getUsername().trim();

            if (!userRequest.getUsername().equals(newUsername)
                    && !userRepository.existsByUsername(newUsername)) {

                throw new RuntimeException("Username is already in use");
            }
            userRequest.setUsername(newUsername);
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim().toLowerCase();

            if (!userRequest.getEmail().equalsIgnoreCase(newEmail)
                    && !userRepository.existsByUsername(newEmail)) {
                throw new RuntimeException("Email is already in use");
            }
            userRequest.setEmail(newEmail);
        }

        User user = userRepository.save(userRequest);

        return userMapper.toUserDTO(user);
    }

    @Transactional
    public void deleteMe(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id"));

        likeRepository.deleteByUserId(userId);
        minhaListaRepository.deleteByUserId(userId);

        userRepository.delete(user);
    }
}