package com.lucaflix.dto.response.dashboard;

import lombok.Data;

@Data
public class UserDashboardDTO {

    private long totalUsers;

    private long freePlan;
    private long premiumPlan;
    private long maxPlan;

    private long blockedUsers;

    private long admins;
    private long users;
    private long superAdmins;
}