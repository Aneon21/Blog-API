package com.blog.api.mappers.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private String dateOfBirth;
    private String bio;
}
