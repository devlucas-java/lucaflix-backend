package com.lucaflix.repository;

import com.lucaflix.model.User;
import com.lucaflix.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(TRIM(u.username)) = LOWER(TRIM(:value)) OR LOWER(TRIM(u.email)) = LOWER(TRIM(:value))")
    Optional<User> findByUsernameOrEmail(@Param("value") String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(TRIM(u.username)) = LOWER(TRIM(:username))")
    boolean existsByUsernameIgnoreCase(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    List<User> findByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.isAccountEnabled = true")
    List<User> findAllEnabledUsers();

    @Query("SELECT u FROM User u WHERE u.isAccountLocked = true")
    List<User> findAllLockedUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByUsernameOrEmailContaining(@Param("searchTerm") String searchTerm);
}