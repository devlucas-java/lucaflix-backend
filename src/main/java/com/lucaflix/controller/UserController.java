package com.lucaflix.controller;

import com.lucaflix.dto.request.user.UpdateUserDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.model.User;
import com.lucaflix.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints de gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal User user) {

        UserDTO response = userService.getMe(user);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDTO> updateMe(
            @Valid @RequestBody UpdateUserDTO request,
            @AuthenticationPrincipal User user) {

        UserDTO response = userService.updateMe(user, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Object> deleteMe(@AuthenticationPrincipal User user) {

        userService.deleteMe(user.getId());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}