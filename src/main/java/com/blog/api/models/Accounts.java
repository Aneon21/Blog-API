package com.blog.api.models;

import com.blog.api.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Accounts {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String username;
    private String password;
    private Roles role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String refreshToken;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Users user;
}
