package com.lucaflix.repository;

import com.lucaflix.model.AdminPanel;
import com.lucaflix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminPanelRepository extends JpaRepository<AdminPanel, UUID> {

    Optional<AdminPanel> findByUser(User user);

    @Query("SELECT ap FROM AdminPanel ap WHERE ap.user.id = :userId")
    Optional<AdminPanel> findByUserId(@Param("userId") UUID userId);

    boolean existsByUser(User user);

    @Query("SELECT CASE WHEN COUNT(ap) > 0 THEN true ELSE false END FROM AdminPanel ap WHERE ap.user.id = :userId")
    boolean existsByUserId(@Param("userId") UUID userId);

    void deleteByUser(User user);

    @Query("DELETE FROM AdminPanel ap WHERE ap.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}