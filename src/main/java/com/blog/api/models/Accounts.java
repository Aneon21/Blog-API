package com.blog.api.models;

import com.blog.api.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Accounts {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private Roles role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String refreshToken;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Users user;
}
