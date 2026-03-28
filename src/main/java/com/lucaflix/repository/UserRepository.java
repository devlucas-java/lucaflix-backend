package com.lucaflix.repository;

import com.lucaflix.model.User;
import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    boolean existsByUsername(String username);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE LOWER(TRIM(u.username)) = LOWER(TRIM(:value)) " +
            "OR LOWER(TRIM(u.email)) = LOWER(TRIM(:value))")
    Optional<User> findByUsernameOrEmail(@Param("value") String usernameOrEmail);

    long countByPlan(Plan plan);

    long countByIsAccountLockedTrue();

    long countByRole(Role role);
}