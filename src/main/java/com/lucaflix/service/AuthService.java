package com.lucaflix.service;

import com.lucaflix.dto.mapper.UserMapper;
import com.lucaflix.dto.request.auth.LoginDTO;
import com.lucaflix.dto.request.auth.RegisterDTO;
import com.lucaflix.dto.request.auth.UpdatePasswordDTO;
import com.lucaflix.dto.request.auth.VerifyPasswordDTO;
import com.lucaflix.dto.response.auth.JwtAuthDTO;
import com.lucaflix.dto.response.others.BooleanDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.exception.ConflictException;
import com.lucaflix.exception.InvalidCredentialsException;
import com.lucaflix.exception.ResourceNotFoundException;
import com.lucaflix.model.User;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.security.JwtService;
import com.lucaflix.service.utils.GenerateUsernames;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GenerateUsernames generateUsernames;

    public JwtAuthDTO login(LoginDTO request) {

        SanitizeUtils.sanitizeStrings(request);
        User user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        String jwt = jwtService.generateAccessToken(user);
        String jwtRefresh = jwtService.generateRefreshToken(user);
        UserDTO response = userMapper.toUserDTO(user);

        return JwtAuthDTO.builder()
                .accessToken(jwt)
                .refreshToken(jwtRefresh)
                .user(response)
                .build();
    }

    @Transactional
    public JwtAuthDTO register(RegisterDTO request) {

        SanitizeUtils.sanitizeStrings(request);
        User userRequest = userMapper.toUser(request);

        String hash = passwordEncoder.encode(request.getPassword());
        userRequest.setPassword(hash);

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new ConflictException("This email is already in use");
        }
        userRequest.setUsername(generateUsernames.generateRandomUsername(userRequest));

        User user = userRepository.save(userRequest);
        UserDTO response = userMapper.toUserDTO(user);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        userRequest.getPassword()
                )
        );
        String jwt = jwtService.generateAccessToken(user);
        String jwtRefresh = jwtService.generateRefreshToken(user);
        return JwtAuthDTO.builder()
                .accessToken(jwt)
                .refreshToken(jwtRefresh)
                .user(response)
                .build();
    }

    @Transactional
    public void updatePassword(User userRequest, UpdatePasswordDTO request) {

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SanitizeUtils.sanitizeStrings(request);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String hash = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hash);

        userRepository.save(user);
    }

    public BooleanDTO verifyPassword(User userRequest, VerifyPasswordDTO request) {

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SanitizeUtils.sanitizeStrings(request);

        if (!passwordEncoder.matches(user.getPassword(), request.getPassword())) {
            return BooleanDTO.builder()
                    .bool(false)
                    .build();
        }

        return BooleanDTO.builder()
                .bool(true)
                .build();
    }
}