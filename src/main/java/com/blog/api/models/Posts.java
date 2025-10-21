package com.blog.api.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    private Users author;

    private String title;

    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
