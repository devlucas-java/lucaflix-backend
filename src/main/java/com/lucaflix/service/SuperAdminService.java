package com.lucaflix.service;

import com.lucaflix.model.User;
import com.lucaflix.model.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminService {

    private final UserService userService;

    /**
     * Faz upgrade do role do usuário
     * USER -> ADMIN -> SUPER_ADMIN
     * Se o usuário já for SUPER_ADMIN, lança exceção
     */
    @Transactional
    public User upgradeUserRole(UUID userId) {
        User user = userService.getUserById(userId);

        switch (user.getRole()) {
            case USER:
                userService.promoteToAdmin(userId, 1);
                user = userService.getUserById(userId); // Refresh user data
                log.info("Usuário {} promovido de USER para ADMIN", userId);
                return user;

            case ADMIN:
                userService.promoteToSuperAdmin(userId);
                user = userService.getUserById(userId); // Refresh user data
                log.info("Usuário {} promovido de ADMIN para SUPER_ADMIN", userId);
                return user;

            case SUPER_ADMIN:
                throw new IllegalStateException("Usuário já possui o role máximo (SUPER_ADMIN)");

            default:
                throw new IllegalStateException("Role não reconhecido: " + user.getRole());
        }
    }

    /**
     * Faz downgrade do role do usuário
     * SUPER_ADMIN -> ADMIN -> USER
     * Se o usuário já for USER, lança exceção
     */
    @Transactional
    public User downgradeUserRole(UUID userId) {
        User user = userService.getUserById(userId);

        switch (user.getRole()) {
            case SUPER_ADMIN:
                // Downgrade para ADMIN
                user.setRole(Role.ADMIN);
                userService.promoteToAdmin(userId, 1); // Redefine como admin com nível 1
                user = userService.getUserById(userId); // Refresh user data
                log.info("Usuário {} rebaixado de SUPER_ADMIN para ADMIN", userId);
                return user;

            case ADMIN:
                // Downgrade para USER
                userService.demoteToUser(userId);
                user = userService.getUserById(userId); // Refresh user data
                log.info("Usuário {} rebaixado de ADMIN para USER", userId);
                return user;

            case USER:
                throw new IllegalStateException("Usuário já possui o role mínimo (USER)");

            default:
                throw new IllegalStateException("Role não reconhecido: " + user.getRole());
        }
    }

    /**
     * Exclui/deleta um usuário do sistema
     * Remove completamente o usuário e todos os dados relacionados
     */
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userService.getUserById(userId);

        // Não permite que super admin delete outro super admin
        // (medida de segurança adicional)
        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new IllegalStateException("Não é possível deletar um usuário SUPER_ADMIN");
        }

        userService.deleteUser(userId);
        log.info("Usuário {} foi completamente deletado do sistema", userId);
    }

    /**
     * Atualiza o plano do usuário por 30 dias
     * Habilita a conta e remove bloqueios
     */
    @Transactional
    public User updateUserPlan(UUID userId) {
        User user = userService.getUserById(userId);

        // Habilita a conta
        userService.setAccountEnabled(userId, true);

        // Remove bloqueio se existir
        userService.setAccountLocked(userId, false);

        // Redefine credenciais como não expiradas
        user.setIsCredentialsExpired(false);
        user.setIsAccountExpired(false);

        user = userService.getUserById(userId); // Refresh user data
        log.info("Plano do usuário {} foi atualizado por 30 dias", userId);

        return user;
    }

    /**
     * Corta/suspende o plano do usuário
     * Desabilita a conta e bloqueia o acesso
     */
    @Transactional
    public User cutUserPlan(UUID userId) {
        User user = userService.getUserById(userId);

        // Não permite cortar plano de super admin
        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new IllegalStateException("Não é possível cortar o plano de um usuário SUPER_ADMIN");
        }

        // Desabilita a conta
        userService.setAccountEnabled(userId, false);

        // Bloqueia a conta
        userService.setAccountLocked(userId, true);

        user = userService.getUserById(userId); // Refresh user data
        log.info("Plano do usuário {} foi cortado/suspenso", userId);

        return user;
    }

    /**
     * Verifica se o usuário atual tem permissão de Super Admin
     */
    public boolean isSuperAdmin(User user) {
        if (user == null) {
            return false;
        }
        return user.getRole() == Role.SUPER_ADMIN;
    }

    /**
     * Valida se o usuário logado tem permissão para executar ações de super admin
     */
    public void validateSuperAdminPermission(User currentUser) {
        if (!isSuperAdmin(currentUser)) {
            throw new SecurityException("Acesso negado. Apenas SUPER_ADMIN pode executar esta ação.");
        }
    }

    /**
     * Obtem informações do usuário por ID (método auxiliar)
     */
    public User getUserInfo(UUID userId) {
        return userService.getUserById(userId);
    }
}