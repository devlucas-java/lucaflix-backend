package com.lucaflix.model;

import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Plan plan = Plan.FREE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "account_enabled")
    private Boolean isAccountEnabled = true;

    @Column(name = "account_locked")
    private Boolean isAccountLocked = false;

    @Column(name = "credentials_expired")
    private Boolean isCredentialsExpired = false;

    @Column(name = "account_expired")
    private Boolean isAccountExpired = false;

    @Column(name = "date_registered")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateRegistered = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Like> likes;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MyListItem myList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return isAccountEnabled;
    }
}