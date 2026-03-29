package com.lucaflix.controller;

import com.lucaflix.dto.request.user.UpdateUserDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Plan;
import com.lucaflix.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID id) {
        UserDTO response = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<UserDTO> promoteUser(@PathVariable UUID id) {
        UserDTO response = userService.promoteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/demote")
    public ResponseEntity<UserDTO> demoteUser(@PathVariable UUID id) {
        UserDTO response = userService.demoteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/plan/{plan}")
    public ResponseEntity<UserDTO> updatePlan(@PathVariable UUID id, @PathVariable Plan plan) {
        UserDTO response = userService.updatePlan(id, plan);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<UserDTO> lockUser(@PathVariable UUID id) {
        UserDTO response = userService.LockeUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<UserDTO> unlockUser(@PathVariable UUID id) {
        UserDTO response = userService.unLockUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}