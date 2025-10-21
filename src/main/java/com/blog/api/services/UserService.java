package com.blog.api.services;

import com.blog.api.mappers.responses.PostsResponse;
import com.blog.api.mappers.responses.UserResponse;
import com.blog.api.models.Accounts;
import com.blog.api.models.Users;
import com.blog.api.repositories.AccountsRepository;
import com.blog.api.repositories.UsersRepository;
import com.blog.api.security.principals.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final AccountsRepository accountsRepository;

    @Transactional
    public void deleteUser(long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Accounts account = accountsRepository.findById(id).orElseThrow(()-> new RuntimeException("The passed in id is invalid"));

        if(!principal.getUsername().equals(account.getUsername())){
            throw new RuntimeException("You are not authorised to delete this account");
        }

        usersRepository.delete(account.getUser());
    }

    public UserResponse getUser(long id){
        Users user = usersRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("No user for the given id found"));
        return UserResponse.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .dateOfBirth(user.getDateOfBirth())
                .bio(user.getBio())
                .build();
    }

    public Set<PostsResponse> getAllPosts(long id){
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No user for the given id found"));

        return user.getPosts().stream().map(post -> {
            return PostsResponse.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .author(
                            UserResponse.builder()
                                    .firstname(post.getAuthor().getFirstname())
                                    .lastname(post.getAuthor().getLastname())
                                    .dateOfBirth(post.getAuthor().getDateOfBirth())
                                    .bio(post.getAuthor().getBio())
                                    .build()
                    )
                    .build();
        }).collect(Collectors.toCollection(HashSet::new));
    }
}
