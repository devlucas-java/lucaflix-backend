package com.lucaflix.service;

import com.lucaflix.dto.mapper.UserMapper;
import com.lucaflix.dto.request.auth.*;
import com.lucaflix.dto.response.auth.JwtAuthDTO;
import com.lucaflix.dto.response.others.BooleanDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.model.User;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public JwtAuthDTO login(LoginDTO request) {

        User user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        String jwt = tokenProvider.generateToken(authentication);
        UserDTO response = userMapper.toUserDTO(user);

        return JwtAuthDTO.builder()
                .accessToken(jwt)
                .user(response)
                .build();
    }

    @Transactional
    public JwtAuthDTO register(RegisterDTO request) {

        User userRequest = userMapper.toUser(request);

        String hash = passwordEncoder.encode(request.getPassword());
        userRequest.setPassword(hash);

        User user = userRepository.save(userRequest);
        UserDTO response = userMapper.toUserDTO(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );
        String jwt = tokenProvider.generateToken(authentication);
        return JwtAuthDTO.builder()
                .accessToken(jwt)
                .user(response)
                .build();
    }

    @Transactional
    public void updatePassword(User userRequest, UpdatePasswordDTO request) {

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(user.getPassword(), request.getCurrentPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        String hash = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hash);

        userRepository.save(user);
    }

    @Transactional
    public void updateEmail(User userRequest, UpdateEmailDTO request) {

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(request.getCurrentPassword())) {
            throw new RuntimeException("Incorrect Email");
        }
        user.setEmail(request.getNewEmail());
        userRepository.save(user);
    }

    public BooleanDTO verifyPassword(User userRequest, VerifyPasswordDTO request) {

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(user.getPassword(), request.getPassword())){
            return BooleanDTO.builder()
                    .bool(false)
                    .build();
        };
        return BooleanDTO.builder()
                .bool(true)
                .build();
    }
}