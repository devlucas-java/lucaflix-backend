package com.lucaflix.repository;

import com.lucaflix.model.Like;
import com.lucaflix.model.Media;
import com.lucaflix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Verifica se o usuário já curtiu a mídia
    boolean existsByUserAndMedia(User user, Media media);

    // Busca o like específico do usuário na mídia
    Optional<Like> findByUserAndMedia(User user, Media media);

    void deleteByMedia(Media media);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

}