package com.lucaflix.model;

import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    // CORRIGIDO: Removido CascadeType.ALL para evitar exclusão das mídias
    // Os likes serão deletados quando o usuário for deletado, mas as mídias permanecem
    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<Like> likes;

    // CORRIGIDO: Removido CascadeType.ALL para evitar exclusão das mídias
    // Os itens da lista serão deletados quando o usuário for deletado, mas as mídias permanecem
    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<MinhaLista> minhaLista;

    // CORRIGIDO: Mantém cascade para AdminPanel pois é específico do usuário
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AdminPanel adminPanel;

    // UserDetails implementation
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