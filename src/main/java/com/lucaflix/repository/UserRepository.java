package com.lucaflix.repository;

import com.lucaflix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(TRIM(u.username)) = LOWER(TRIM(:value)) OR LOWER(TRIM(u.email)) = LOWER(TRIM(:value))")
    Optional<User> findByUsernameOrEmail(@Param("value") String usernameOrEmail);

    @Query("SELECT u FROM User u WHERE "
            + "(LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ")
    Page<User> findBySearchTerm(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}