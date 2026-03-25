package com.lucaflix.controller;

import com.lucaflix.dto.request.auth.*;
import com.lucaflix.dto.response.others.BooleanDTO;
import com.lucaflix.dto.response.auth.JwtAuthDTO;
import com.lucaflix.model.User;
import com.lucaflix.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthDTO> login(@Valid @RequestBody LoginDTO login) {
        JwtAuthDTO response = authService.login(login);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthDTO> register(@Valid @RequestBody RegisterDTO register) {

        JwtAuthDTO response = authService.register(register);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update-password")
    public ResponseEntity<Object> changePassword(
            @Valid @RequestBody UpdatePasswordDTO password,
            @AuthenticationPrincipal User user) {

            authService.updatePassword(user, password);

            return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/verify-password")
    public ResponseEntity<BooleanDTO> verifyPassword(
            @RequestBody VerifyPasswordDTO request,
            @AuthenticationPrincipal User user) {

            BooleanDTO response = authService.verifyPassword(user, request);

            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}