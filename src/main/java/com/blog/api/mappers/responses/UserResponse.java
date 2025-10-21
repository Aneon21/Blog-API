package com.blog.api.mappers.responses;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String bio;
}
