package com.lucaflix.service;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.dto.user.UserDTO;
import com.lucaflix.dto.user.UserMapper;
import com.lucaflix.model.User;
import com.lucaflix.model.Movie;
import com.lucaflix.model.Serie;
import com.lucaflix.model.Anime;
import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MinhaListaRepository;
import com.lucaflix.repository.MovieRepository;
import com.lucaflix.repository.SerieRepository;
import com.lucaflix.repository.AnimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final MovieRepository movieRepository;
    private final SerieRepository serieRepository;
    private final AnimeRepository animeRepository;

    // ==================== BUSCA DE USUÁRIOS ====================

    public PaginatedResponseDTO<UserDTO.UserListResponse> searchUsers(String searchTerm, Pageable pageable) {
        Page<User> users;

        if (StringUtils.isEmpty(searchTerm)) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findBySearchTerm(searchTerm, pageable);
        }

        List<UserDTO.UserListResponse> responses = users.getContent().stream()
                .map(this::convertToUserListResponse)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                responses,
                users.getNumber(),
                users.getTotalPages(),
                users.getTotalElements(),
                users.getSize(),
                users.isFirst(),
                users.isLast(),
                users.hasNext(),
                users.hasPrevious()
        );
    }

    private UserDTO.UserListResponse convertToUserListResponse(User user) {
        return new UserDTO.UserListResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name(),
                user.getIsAccountEnabled(),
                user.getIsAccountLocked()
        );
    }

    // ==================== PROMOÇÃO DE USUÁRIOS ====================

    @Transactional
    public User promoteUser(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);
        validateNotSelfAction(userId, currentUser, "promover");

        User user = userService.getUserById(userId);

        switch (user.getRole()) {
            case USER:
                userService.promoteToAdmin(userId, 1);
                user = userService.getUserById(userId);
                log.info("Usuário {} ({}) promovido de USER para ADMIN por SuperAdmin {} ({})",
                        user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());
                break;

            case ADMIN:
                userService.promoteToSuperAdmin(userId);
                user = userService.getUserById(userId);
                log.info("Usuário {} ({}) promovido de ADMIN para SUPER_ADMIN por SuperAdmin {} ({})",
                        user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());
                break;

            case SUPER_ADMIN:
                throw new IllegalStateException("Usuário já possui o role máximo (SUPER_ADMIN)");

            default:
                throw new IllegalStateException("Role não reconhecido: " + user.getRole());
        }

        return user;
    }

    // ==================== REBAIXAMENTO DE USUÁRIOS ====================

    @Transactional
    public User demoteUser(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);
        validateNotSelfAction(userId, currentUser, "rebaixar");

        User user = userService.getUserById(userId);

        switch (user.getRole()) {
            case SUPER_ADMIN:
                user.setRole(Role.ADMIN);
                userService.promoteToAdmin(userId, 1);
                user = userService.getUserById(userId);
                log.info("Usuário {} ({}) rebaixado de SUPER_ADMIN para ADMIN por SuperAdmin {} ({})",
                        user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());
                break;

            case ADMIN:
                userService.demoteToUser(userId);
                user = userService.getUserById(userId);
                log.info("Usuário {} ({}) rebaixado de ADMIN para USER por SuperAdmin {} ({})",
                        user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());
                break;

            case USER:
                throw new IllegalStateException("Usuário já possui o role mínimo (USER)");

            default:
                throw new IllegalStateException("Role não reconhecido: " + user.getRole());
        }

        return user;
    }

    // ==================== EXCLUSÃO DE USUÁRIOS ====================

    @Transactional
    public void deleteUser(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);
        validateNotSelfAction(userId, currentUser, "deletar");

        User user = userService.getUserById(userId);

        // Não permite que super admin delete outro super admin
        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new IllegalStateException("Não é possível deletar um usuário SUPER_ADMIN");
        }

        String deletedUsername = user.getUsername();
        userService.deleteUserAndRelatedData(userId);
        log.info("Usuário {} ({}) foi completamente deletado do sistema por SuperAdmin {} ({})",
                deletedUsername, userId, currentUser.getUsername(), currentUser.getId());
    }

    // ==================== GERENCIAMENTO DE PLANOS ====================

    @Transactional
    public User updateUserPlan(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        User user = userService.getUserById(userId);

        user.setIsCredentialsExpired(false);
        user.setIsAccountExpired(false);
        userService.setAccountLocked(userId, false);
        user.setPlan(Plan.PREMIUM);
        userRepository.save(user);

        user = userService.getUserById(userId);
        log.info("Plano do usuário {} ({}) foi atualizado para PREMIUM por SuperAdmin {} ({})",
                user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());

        return user;
    }

    @Transactional
    public User cutUserPlan(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);
        validateNotSelfAction(userId, currentUser, "cortar o plano");

        User user = userService.getUserById(userId);

        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new IllegalStateException("Não é possível cortar o plano de um usuário SUPER_ADMIN");
        }

        user.setPlan(Plan.FREE);
        userRepository.save(user);
        log.info("Plano do usuário {} ({}) foi cortado para FREE por SuperAdmin {} ({})",
                user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());

        return user;
    }

    // ==================== BLOQUEIO/DESBLOQUEIO DE USUÁRIOS ====================

    @Transactional
    public User blockUser(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);
        validateNotSelfAction(userId, currentUser, "bloquear");

        User user = userService.getUserById(userId);

        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new IllegalStateException("Não é possível bloquear um usuário SUPER_ADMIN");
        }

        userService.setAccountLocked(userId, true);
        user = userService.getUserById(userId);
        log.info("Usuário {} ({}) foi bloqueado por SuperAdmin {} ({})",
                user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());

        return user;
    }

    @Transactional
    public User unblockUser(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        User user = userService.getUserById(userId);

        userService.setAccountLocked(userId, false);
        user.setIsCredentialsExpired(false);
        user.setIsAccountExpired(false);
        userRepository.save(user);

        user = userService.getUserById(userId);
        log.info("Usuário {} ({}) foi desbloqueado por SuperAdmin {} ({})",
                user.getUsername(), userId, currentUser.getUsername(), currentUser.getId());

        return user;
    }

    // ==================== REMOÇÃO DE LIKES POR CONTEÚDO ====================

    @Transactional
    public void removeAllMovieLikes(Long movieId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado com ID: " + movieId));

        long likesRemoved = likeRepository.countByMovie(movie);
        likeRepository.deleteByMovie(movie);

        log.info("Todos os {} likes do filme '{}' (ID: {}) foram removidos por SuperAdmin {} ({})",
                likesRemoved, movie.getTitle(), movieId, currentUser.getUsername(), currentUser.getId());
    }

    @Transactional
    public void removeAllSerieLikes(Long serieId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new IllegalArgumentException("Série não encontrada com ID: " + serieId));

        long likesRemoved = likeRepository.countBySerie(serie);
        likeRepository.deleteBySerie(serie);

        log.info("Todos os {} likes da série '{}' (ID: {}) foram removidos por SuperAdmin {} ({})",
                likesRemoved, serie.getTitle(), serieId, currentUser.getUsername(), currentUser.getId());
    }

    @Transactional
    public void removeAllAnimeLikes(Long animeId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new IllegalArgumentException("Anime não encontrado com ID: " + animeId));

        long likesRemoved = likeRepository.countByAnime(anime);
        likeRepository.deleteByAnime(anime);

        log.info("Todos os {} likes do anime '{}' (ID: {}) foram removidos por SuperAdmin {} ({})",
                likesRemoved, anime.getTitle(), animeId, currentUser.getUsername(), currentUser.getId());
    }

    // ==================== REMOÇÃO DE LISTAS POR CONTEÚDO ====================

    @Transactional
    public void removeAllMovieFromLists(Long movieId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado com ID: " + movieId));

        long itemsRemoved = minhaListaRepository.countByMovie(movie);
        minhaListaRepository.deleteByMovie(movie);

        log.info("Filme '{}' (ID: {}) foi removido de {} listas por SuperAdmin {} ({})",
                movie.getTitle(), movieId, itemsRemoved, currentUser.getUsername(), currentUser.getId());
    }

    @Transactional
    public void removeAllSerieFromLists(Long serieId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new IllegalArgumentException("Série não encontrada com ID: " + serieId));

        long itemsRemoved = minhaListaRepository.countBySerie(serie);
        minhaListaRepository.deleteBySerie(serie);

        log.info("Série '{}' (ID: {}) foi removida de {} listas por SuperAdmin {} ({})",
                serie.getTitle(), serieId, itemsRemoved, currentUser.getUsername(), currentUser.getId());
    }

    @Transactional
    public void removeAllAnimeFromLists(Long animeId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new IllegalArgumentException("Anime não encontrado com ID: " + animeId));

        long itemsRemoved = minhaListaRepository.countByAnime(anime);
        minhaListaRepository.deleteByAnime(anime);

        log.info("Anime '{}' (ID: {}) foi removido de {} listas por SuperAdmin {} ({})",
                anime.getTitle(), animeId, itemsRemoved, currentUser.getUsername(), currentUser.getId());
    }

    // ==================== LIMPEZA COMPLETA DE CONTEÚDO ====================

    @Transactional
    public void cleanAllMovieInteractions(Long movieId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado com ID: " + movieId));

        long likesRemoved = likeRepository.countByMovie(movie);
        long listsRemoved = minhaListaRepository.countByMovie(movie);

        likeRepository.deleteByMovie(movie);
        minhaListaRepository.deleteByMovie(movie);

        log.info("Todas as interações do filme '{}' (ID: {}) foram limpas: {} likes e {} itens de lista removidos por SuperAdmin {} ({})",
                movie.getTitle(), movieId, likesRemoved, listsRemoved, currentUser.getUsername(), currentUser.getId());
    }

    @Transactional
    public void cleanAllSerieInteractions(Long serieId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new IllegalArgumentException("Série não encontrada com ID: " + serieId));

        long likesRemoved = likeRepository.countBySerie(serie);
        long listsRemoved = minhaListaRepository.countBySerie(serie);

        likeRepository.deleteBySerie(serie);
        minhaListaRepository.deleteBySerie(serie);

        log.info("Todas as interações da série '{}' (ID: {}) foram limpas: {} likes e {} itens de lista removidos por SuperAdmin {} ({})",
                serie.getTitle(), serieId, likesRemoved, listsRemoved, currentUser.getUsername(), currentUser.getId());
    }

    @Transactional
    public void cleanAllAnimeInteractions(Long animeId, User currentUser) {
        validateSuperAdminPermission(currentUser);

        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new IllegalArgumentException("Anime não encontrado com ID: " + animeId));

        long likesRemoved = likeRepository.countByAnime(anime);
        long listsRemoved = minhaListaRepository.countByAnime(anime);

        likeRepository.deleteByAnime(anime);
        minhaListaRepository.deleteByAnime(anime);

        log.info("Todas as interações do anime '{}' (ID: {}) foram limpas: {} likes e {} itens de lista removidos por SuperAdmin {} ({})",
                anime.getTitle(), animeId, likesRemoved, listsRemoved, currentUser.getUsername(), currentUser.getId());
    }

    // ==================== UTILITÁRIOS ====================

    public boolean isSuperAdmin(User user) {
        return user != null && user.getRole() == Role.SUPER_ADMIN;
    }

    public void validateSuperAdminPermission(User currentUser) {
        if (!isSuperAdmin(currentUser)) {
            throw new SecurityException("Acesso negado. Apenas SUPER_ADMIN pode executar esta ação.");
        }
    }

    private void validateNotSelfAction(UUID targetUserId, User currentUser, String action) {
        if (targetUserId.equals(currentUser.getId())) {
            throw new IllegalStateException("Não é possível " + action + " a si mesmo");
        }
    }

    public User getUserInfo(UUID userId, User currentUser) {
        validateSuperAdminPermission(currentUser);
        return userService.getUserById(userId);
    }

    // ==================== DTOs ====================

    @lombok.Builder
    @lombok.Data
    public static class ContentStatsDTO {
        private long totalMovieLikes;
        private long totalSerieLikes;
        private long totalAnimeLikes;
        private long totalMovieInLists;
        private long totalSerieInLists;
        private long totalAnimeInLists;
        private long totalUsersWithLists;
        private long totalUsers;
        private long totalAdmins;
        private long totalSuperAdmins;
    }

    @lombok.Builder
    @lombok.Data
    public static class UserStatsDTO {
        private long totalUsers;
        private long totalActiveUsers;
        private long totalBlockedUsers;
        private long totalPremiumUsers;
        private long totalFreeUsers;
        private long totalAdmins;
        private long totalSuperAdmins;
    }
}