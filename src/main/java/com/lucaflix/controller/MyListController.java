package com.lucaflix.controller;

import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.MediaType;
import com.lucaflix.service.MyListItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/my-list")
@RequiredArgsConstructor
public class MyListController {

    private final MyListItemService myListItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<Object>> getMyList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestBody FilterDTO filter) {

        PaginatedResponseDTO<Object> response = myListItemService.getMyList(user, filter, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/{type}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> addMyList(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @PathVariable MediaType type
    ) {

        myListItemService.addMyList(id, user, type);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public  ResponseEntity<Void> removeMyList(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @PathVariable MediaType type
    ){
        myListItemService.removeMyList(id, user, type);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}