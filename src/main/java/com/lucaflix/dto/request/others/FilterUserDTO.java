package com.lucaflix.dto.request.others;

import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterUserDTO {

    private String username;
    private String email;
    private String firstName;
    private String lastName;

    private Plan plan;
    private Role role;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isAccountEnabled;
    private Boolean isAccountLocked;
}