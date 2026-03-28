package com.lucaflix.service;

import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.mapper.UserMapper;
import com.lucaflix.dto.request.others.FilterUserDTO;
import com.lucaflix.dto.request.user.UpdateUserDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.utils.spec.UserSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;

    public UserDTO getMe(User userRequest) {
        User user = userRepository.findById(userRequest.getId()).
                orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDTO(user);
    }

    public UserDTO getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserDTO(user);
    }

    public PaginatedResponseDTO<UserDTO> filterUser(FilterUserDTO filter, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        UserSpecification spec = new UserSpecification(filter);

        Page<User> userPage = userRepository.findAll(spec, pageable);
        return pageMapper.toPaginatedDTO(userPage, userMapper::toUserDTO);
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
                    && userRepository.existsByUsername(newUsername)) {

                throw new RuntimeException("Username is already in use");
            }
            userRequest.setUsername(newUsername);
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim().toLowerCase();

            if (!userRequest.getEmail().equalsIgnoreCase(newEmail)
                    && userRepository.existsByUsername(newEmail)) {
                throw new RuntimeException("Email is already in use");
            }
            userRequest.setEmail(newEmail);
        }

        User user = userRepository.save(userRequest);
        return userMapper.toUserDTO(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id"));

        userRepository.delete(user);
    }

    public UserDTO demoteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id"));

        if (user.getRole() == Role.USER) {
            throw new RuntimeException("User already has the role lowest, USER");
        }
        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.USER);
        }
        if (user.getRole() == Role.SUPERADMIN) {
            user.setRole(Role.ADMIN);
        }
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    public UserDTO promoteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id"));

        if (user.getRole() == Role.SUPERADMIN) {
            throw new RuntimeException("User already has max role, SUPERADMIN");
        }
        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.SUPERADMIN);
        }
        if (user.getRole() == Role.USER) {
            user.setRole(Role.ADMIN);
        }
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    public UserDTO LockeUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found by id"));

        if (user.getRole() == Role.SUPERADMIN) {
            throw new RuntimeException("Not possible to block SUPERADMIN");
        }

        user.setIsAccountLocked(true);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    public UserDTO unLockUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found by id"));

        user.setIsAccountLocked(false);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    public UserDTO updatePlan(UUID id, Plan plan) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found by id"));

        user.setPlan(plan);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }
}