package com.lucaflix.service;

import com.lucaflix.dto.mapper.UserMapper;
import com.lucaflix.dto.request.auth.LoginDTO;
import com.lucaflix.dto.request.auth.RegisterDTO;
import com.lucaflix.dto.request.auth.UpdatePasswordDTO;
import com.lucaflix.dto.request.auth.VerifyPasswordDTO;
import com.lucaflix.dto.response.auth.JwtAuthDTO;
import com.lucaflix.dto.response.others.BooleanDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.exception.InvalidCredentialsException;
import com.lucaflix.model.User;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.security.JwtService;
import com.lucaflix.service.utils.GenerateUsernames;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setFirstName("Lucas");
        user.setLastName("Macedo");
        user.setUsername("lucas-macedo");
        user.setEmail("lucas@email.com");
        user.setPassword("hashed_password");

        GenerateUsernames generateUsernames = new GenerateUsernames(userRepository);

        authService = new AuthService(
                authenticationManager,
                userRepository,
                userMapper,
                passwordEncoder,
                jwtService,
                generateUsernames
        );
    }

    // -------------------------------------------------------------------------
    // login
    // -------------------------------------------------------------------------

    @Test
    void login_ShouldReturnJwtAuthDTO_WhenCredentialsAreValid() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLogin("lucas@email.com");
        loginDTO.setPassword("password123");

        UserDTO userDTO = new UserDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(loginDTO)).thenAnswer(inv -> null);

            when(userRepository.findByUsernameOrEmail("lucas@email.com")).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any())).thenReturn(null);
            when(jwtService.generateAccessToken(user)).thenReturn("access-token");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
            when(userMapper.toUserDTO(user)).thenReturn(userDTO);

            JwtAuthDTO result = authService.login(loginDTO);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("access-token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(result.getUser()).isEqualTo(userDTO);
        }
    }

    @Test
    void login_ShouldCallAuthenticationManager() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLogin("lucas@email.com");
        loginDTO.setPassword("password123");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(loginDTO)).thenAnswer(inv -> null);

            when(userRepository.findByUsernameOrEmail("lucas@email.com")).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any())).thenReturn(null);
            when(jwtService.generateAccessToken(user)).thenReturn("access-token");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
            when(userMapper.toUserDTO(user)).thenReturn(new UserDTO());

            authService.login(loginDTO);

            verify(authenticationManager).authenticate(
                    argThat(auth -> auth instanceof UsernamePasswordAuthenticationToken
                            && auth.getPrincipal().equals("lucas@email.com"))
            );
        }
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLogin("unknown@email.com");
        loginDTO.setPassword("password123");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(loginDTO)).thenAnswer(inv -> null);

            when(userRepository.findByUsernameOrEmail("unknown@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(loginDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");

            verify(authenticationManager, never()).authenticate(any());
        }
    }

    @Test
    void login_ShouldCallSanitize() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLogin("lucas@email.com");
        loginDTO.setPassword("password123");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(loginDTO)).thenAnswer(inv -> null);

            when(userRepository.findByUsernameOrEmail("lucas@email.com")).thenReturn(Optional.of(user));
            when(authenticationManager.authenticate(any())).thenReturn(null);
            when(jwtService.generateAccessToken(user)).thenReturn("token");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh");
            when(userMapper.toUserDTO(user)).thenReturn(new UserDTO());

            authService.login(loginDTO);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(loginDTO));
        }
    }

    // -------------------------------------------------------------------------
    // register
    // -------------------------------------------------------------------------

    @Test
    void register_ShouldSaveUserAndReturnJwtAuthDTO() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setPassword("rawPassword");

        UserDTO userDTO = new UserDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(registerDTO)).thenAnswer(inv -> null);

            when(userMapper.toUser(registerDTO)).thenReturn(user);
            when(passwordEncoder.encode("rawPassword")).thenReturn("hashed_password");
            when(userRepository.save(user)).thenReturn(user);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userMapper.toUserDTO(user)).thenReturn(userDTO);
            when(authenticationManager.authenticate(any())).thenReturn(null);
            when(jwtService.generateAccessToken(user)).thenReturn("access-token");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

            JwtAuthDTO result = authService.register(registerDTO);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("access-token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(result.getUser().getEmail()).isEqualTo(userDTO.getEmail());
            verify(userRepository).save(user);
        }
    }

    @Test
    void register_ShouldEncodePasswordBeforeSaving() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setPassword("plaintext");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(registerDTO)).thenAnswer(inv -> null);

            when(userMapper.toUser(registerDTO)).thenReturn(user);
            when(passwordEncoder.encode("plaintext")).thenReturn("encoded");
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toUserDTO(user)).thenReturn(new UserDTO());
            when(authenticationManager.authenticate(any())).thenReturn(null);
            when(jwtService.generateAccessToken(user)).thenReturn("token");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

            authService.register(registerDTO);

            verify(passwordEncoder).encode("plaintext");
            assertThat(user.getPassword()).isEqualTo("encoded");
        }
    }

    @Test
    void register_ShouldCallSanitize() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setPassword("pass");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(registerDTO)).thenAnswer(inv -> null);

            when(userMapper.toUser(registerDTO)).thenReturn(user);
            when(passwordEncoder.encode(any())).thenReturn("hashed");
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toUserDTO(user)).thenReturn(new UserDTO());
            when(authenticationManager.authenticate(any())).thenReturn(null);
            when(jwtService.generateAccessToken(user)).thenReturn("token");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

            authService.register(registerDTO);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(registerDTO));
        }
    }

    // -------------------------------------------------------------------------
    // updatePassword
    // -------------------------------------------------------------------------

    @Test
    void updatePassword_ShouldUpdatePassword_WhenCurrentPasswordMatches() {
        UpdatePasswordDTO updateDTO = new UpdatePasswordDTO();
        updateDTO.setCurrentPassword("hashed_password");
        updateDTO.setNewPassword("newPassword");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(user.getPassword(), updateDTO.getCurrentPassword())).thenReturn(true);
            when(passwordEncoder.encode("newPassword")).thenReturn("new_hashed");

            authService.updatePassword(user, updateDTO);

            verify(passwordEncoder).encode("newPassword");
            verify(userRepository).save(user);
            assertThat(user.getPassword()).isEqualTo("new_hashed");
        }
    }

    @Test
    void updatePassword_ShouldThrowException_WhenCurrentPasswordDoesNotMatch() {
        UpdatePasswordDTO updateDTO = new UpdatePasswordDTO();
        updateDTO.setCurrentPassword("wrong_password");
        updateDTO.setNewPassword("newPassword");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(updateDTO.getCurrentPassword(), user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> authService.updatePassword(user, updateDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid login credentials");

            verify(userRepository, never()).save(any());
        }
    }

    @Test
    void updatePassword_ShouldThrowException_WhenUserNotFound() {
        UpdatePasswordDTO updateDTO = new UpdatePasswordDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.updatePassword(user, updateDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");

            verify(userRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // verifyPassword
    // -------------------------------------------------------------------------

    @Test
    void verifyPassword_ShouldReturnTrue_WhenPasswordMatches() {
        VerifyPasswordDTO verifyDTO = new VerifyPasswordDTO();
        verifyDTO.setPassword("hashed_password");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(verifyDTO)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(user.getPassword(), verifyDTO.getPassword())).thenReturn(true);

            BooleanDTO result = authService.verifyPassword(user, verifyDTO);

            assertThat(result.isBool()).isTrue();
        }
    }

    @Test
    void verifyPassword_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        VerifyPasswordDTO verifyDTO = new VerifyPasswordDTO();
        verifyDTO.setPassword("wrong_password");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(verifyDTO)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(user.getPassword(), verifyDTO.getPassword())).thenReturn(false);

            BooleanDTO result = authService.verifyPassword(user, verifyDTO);

            assertThat(result.isBool()).isFalse();
        }
    }

    @Test
    void verifyPassword_ShouldThrowException_WhenUserNotFound() {
        VerifyPasswordDTO verifyDTO = new VerifyPasswordDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.verifyPassword(user, verifyDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");
        }
    }
}