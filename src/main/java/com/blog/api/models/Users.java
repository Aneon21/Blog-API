package com.blog.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String bio;
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id")
    private Accounts account;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Posts> posts;
}
