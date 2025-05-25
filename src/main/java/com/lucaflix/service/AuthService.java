package com.lucaflix.service;

import com.lucaflix.dto.auth.AuthDTO;
import com.lucaflix.model.User;
import com.lucaflix.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    /// AUTENTICA UM USUARIO E RETORNA TOKEN JWT COM INFORMACOES DO USUARIO
    public AuthDTO.JwtAuthResponse login(AuthDTO.LoginRequest loginRequest) {
        validateLoginRequest(loginRequest);

        try {
            /// VERIFICA SE USUARIO EXISTE E ESTA HABILITADO
            User user = userService.getUserByUsernameOrEmail(loginRequest.getUsernameOrEmail());

            /// VERIFICA STATUS DA CONTA
            validateUserAccountStatus(user);

            /// AUTENTICA USUARIO
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            /// GERA TOKEN JWT
            String jwt = tokenProvider.generateToken(authentication);

            /// CONVERTE USER PARA USER RESPONSE DTO
            AuthDTO.UserResponse userResponse = userService.convertToAuthUserResponse(user);

            log.info("Usuario logado com sucesso: {}", user.getUsername());
            return new AuthDTO.JwtAuthResponse(jwt, userResponse);

        } catch (BadCredentialsException e) {
            log.warn("Tentativa de login falhou para: {}", loginRequest.getUsernameOrEmail());
            throw new BadCredentialsException("Usuario/email ou senha invalidos");
        } catch (AuthenticationException e) {
            log.error("Autenticacao falhou para: {}", loginRequest.getUsernameOrEmail(), e);
            throw new AuthenticationException("Falha na autenticacao: " + e.getMessage()) {};
        }
    }

    /// REGISTRA UM NOVO USUARIO NO SISTEMA
    @Transactional
    public AuthDTO.UserResponse register(AuthDTO.SignUpRequest signUpRequest) {
        validateSignUpRequest(signUpRequest);

        try {
            /// CRIA USUARIO
            User user = userService.createUser(signUpRequest);

            /// CONVERTE PARA USER RESPONSE DTO
            AuthDTO.UserResponse userResponse = userService.convertToAuthUserResponse(user);

            log.info("Usuario registrado com sucesso: {}", user.getUsername());
            return userResponse;

        } catch (IllegalArgumentException e) {
            log.warn("Registro falhou: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado durante registro", e);
            throw new RuntimeException("Registro falhou devido a um erro interno", e);
        }
    }

    /// ALTERA SENHA DO USUARIO
    @Transactional
    public void changePassword(User user, AuthDTO.PasswordChangeRequest request) {
        validatePasswordChangeRequest(request);

        try {
            userService.changePassword(user, request.getCurrentPassword(), request.getNewPassword());
            log.info("Senha alterada com sucesso para usuario: {}", user.getUsername());
        } catch (IllegalArgumentException e) {
            log.warn("Alteracao de senha falhou para usuario {}: {}", user.getUsername(), e.getMessage());
            throw e;
        }
    }

    /// ATUALIZA EMAIL DO USUARIO
    @Transactional
    public void updateEmail(User user, AuthDTO.EmailUpdateRequest request) {
        validateEmailUpdateRequest(request);

        try {
            userService.updateEmail(user, request.getNewEmail(), request.getCurrentPassword());
            log.info("Email atualizado com sucesso para usuario: {}", user.getUsername());
        } catch (IllegalArgumentException e) {
            log.warn("Atualizacao de email falhou para usuario {}: {}", user.getUsername(), e.getMessage());
            throw e;
        }
    }

    /// VERIFICA SENHA DO USUARIO
    public boolean verifyPassword(User user, String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha nao pode estar vazia");
        }
        return userService.verifyPassword(user, password);
    }

    /// OBTEM INFORMACOES DO USUARIO ATUAL
    public AuthDTO.UserResponse getCurrentUserInfo(User user) {
        return userService.convertToAuthUserResponse(user);
    }

    /// METODOS PRIVADOS DE VALIDACAO

    /// VALIDA REQUISICAO DE LOGIN
    private void validateLoginRequest(AuthDTO.LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("Requisicao de login nao pode ser nula");
        }
        if (loginRequest.getUsernameOrEmail() == null || loginRequest.getUsernameOrEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario ou email nao pode estar vazio");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha nao pode estar vazia");
        }
    }

    /// VALIDA REQUISICAO DE REGISTRO
    private void validateSignUpRequest(AuthDTO.SignUpRequest signUpRequest) {
        if (signUpRequest == null) {
            throw new IllegalArgumentException("Requisicao de registro nao pode ser nula");
        }
        if (signUpRequest.getNomeCompleto() == null || signUpRequest.getNomeCompleto().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome completo nao pode estar vazio");
        }
        if (signUpRequest.getNomeCompleto().length() < 2 || signUpRequest.getNomeCompleto().length() > 100) {
            throw new IllegalArgumentException("Nome deve ter entre 2 e 100 caracteres");
        }
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email nao pode estar vazio");
        }
        if (!signUpRequest.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email invalido");
        }
        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha nao pode estar vazia");
        }
        if (signUpRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres");
        }
    }

    /// VALIDA REQUISICAO DE ALTERACAO DE SENHA
    private void validatePasswordChangeRequest(AuthDTO.PasswordChangeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Requisicao de alteracao de senha nao pode ser nula");
        }
        if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha atual nao pode estar vazia");
        }
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Nova senha nao pode estar vazia");
        }
        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("Nova senha deve ter pelo menos 8 caracteres");
        }
    }

    /// VALIDA REQUISICAO DE ATUALIZACAO DE EMAIL
    private void validateEmailUpdateRequest(AuthDTO.EmailUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Requisicao de atualizacao de email nao pode ser nula");
        }
        if (request.getNewEmail() == null || request.getNewEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Novo email nao pode estar vazio");
        }
        if (!request.getNewEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email invalido");
        }
        if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha atual e obrigatoria para verificacao");
        }
    }

    /// VALIDA STATUS DA CONTA DO USUARIO
    private void validateUserAccountStatus(User user) {
        if (!user.getIsAccountEnabled()) {
            throw new RuntimeException("Conta esta desabilitada");
        }
        if (user.getIsAccountLocked()) {
            throw new RuntimeException("Conta esta bloqueada");
        }
        if (user.getIsAccountExpired()) {
            throw new RuntimeException("Conta expirou");
        }
        if (user.getIsCredentialsExpired()) {
            throw new RuntimeException("Credenciais expiraram");
        }
    }
}