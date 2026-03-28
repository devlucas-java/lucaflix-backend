package com.lucaflix.controller;

import com.lucaflix.dto.response.dashboard.DashboardDTO;
import com.lucaflix.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO response = dashboardService.getDashboard();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}