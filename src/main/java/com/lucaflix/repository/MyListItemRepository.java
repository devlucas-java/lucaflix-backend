package com.lucaflix.repository;

import com.lucaflix.model.*;
import com.lucaflix.model.enums.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MyListItemRepository extends JpaRepository<MyListItem, Long> {


    Page<MyListItem> findByUser(User user, Pageable pageable);

    boolean existsByUserAndContentIdAndType(User user, UUID contentId, MediaType type);

    Optional<MyListItem> findByUserAndContentIdAndType(User user, UUID contentId, MediaType type);
}