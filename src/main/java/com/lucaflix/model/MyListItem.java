package com.lucaflix.model;

import com.lucaflix.model.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "my_list")
@Data
public class MyListItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private UUID contentId;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    private LocalDateTime addedAt;
}