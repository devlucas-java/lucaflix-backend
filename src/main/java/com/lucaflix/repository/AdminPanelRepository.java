package com.lucaflix.repository;

import com.lucaflix.model.AdminPanel;
import com.lucaflix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminPanelRepository extends JpaRepository<AdminPanel, UUID> {

    Optional<AdminPanel> findByUser(User user);

    boolean existsByUser(User user);

    @Modifying
    @Query("DELETE FROM AdminPanel ap WHERE ap.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}