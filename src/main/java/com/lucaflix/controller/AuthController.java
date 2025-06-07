package com.lucaflix.controller;

import com.lucaflix.dto.auth.AuthDTO;
import com.lucaflix.model.User;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticação")
public class AuthController {

    private final AuthService authService;

    /// AUTENTICA USUARIO E RETORNA TOKEN JWT
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica um usuário e retorna um token JWT com informações do usuário")
    public ResponseEntity<AuthDTO.JwtAuthResponse> login(@Valid @RequestBody AuthDTO.LoginRequest loginRequest) {
        try {
            AuthDTO.JwtAuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Validação de login falhou: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Login falhou para usuário: {}", loginRequest.getUsernameOrEmail(), e);
            throw e;
        }
    }

    /// REGISTRA NOVO USUARIO NO SISTEMA
    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário no sistema")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody AuthDTO.SignUpRequest signUpRequest) {
        try {
            AuthDTO.UserResponse userResponse = authService.register(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Usuário registrado com sucesso!",
                            "user", userResponse
                    ));
        } catch (IllegalArgumentException e) {
            log.warn("Validação de registro falhou: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Registro falhou para usuário: {}", signUpRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registro falhou devido a um erro interno"));
        }
    }

    /// ALTERA SENHA DO USUARIO ATUAL
    @PostMapping("/change-password")
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário atual")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody AuthDTO.PasswordChangeRequest request,
            @CurrentUser User user) {

        // Check if user is authenticated
        if (user == null) {
            log.warn("Tentativa de alteração de senha sem autenticação válida");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuário não autenticado ou token inválido"));
        }

        try {
            authService.changePassword(user, request);
            return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso"));
        } catch (IllegalArgumentException e) {
            log.warn("Alteração de senha falhou para usuário {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado durante alteração de senha para usuário: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao alterar senha"));
        }
    }

    /// ATUALIZA EMAIL DO USUARIO ATUAL
    @PostMapping("/update-email")
    @Operation(summary = "Atualizar email", description = "Atualiza o endereço de email do usuário atual")
    public ResponseEntity<Map<String, String>> updateEmail(
            @Valid @RequestBody AuthDTO.EmailUpdateRequest request,
            @CurrentUser User user) {

        // Check if user is authenticated
        if (user == null) {
            log.warn("Tentativa de atualização de email sem autenticação válida");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuário não autenticado ou token inválido"));
        }

        try {
            authService.updateEmail(user, request);
            return ResponseEntity.ok(Map.of("message", "Email atualizado com sucesso"));
        } catch (IllegalArgumentException e) {
            log.warn("Atualização de email falhou para usuário {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado durante atualização de email para usuário: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao atualizar email"));
        }
    }

    /// VERIFICA SE SENHA INFORMADA E VALIDA
    @PostMapping("/verify-password")
    @Operation(summary = "Verificar senha", description = "Verifica se a senha fornecida corresponde à senha do usuário atual")
    public ResponseEntity<Map<String, Boolean>> verifyPassword(
            @RequestBody Map<String, String> request,
            @CurrentUser User user) {

        // Check if user is authenticated
        if (user == null) {
            log.warn("Tentativa de verificação de senha sem autenticação válida");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false));
        }

        try {
            String password = request.get("password");
            boolean isValid = authService.verifyPassword(user, password);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (IllegalArgumentException e) {
            log.warn("Verificação de senha falhou para usuário {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("valid", false));
        } catch (Exception e) {
            log.error("Erro inesperado durante verificação de senha para usuário: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("valid", false));
        }
    }

}