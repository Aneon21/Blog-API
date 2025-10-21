package com.blog.api.services;

import com.blog.api.mappers.requests.PostRequest;
import com.blog.api.mappers.responses.PostsResponse;
import com.blog.api.mappers.responses.UserResponse;
import com.blog.api.models.Posts;
import com.blog.api.models.Users;
import com.blog.api.repositories.AccountsRepository;
import com.blog.api.repositories.PostsRepository;
import com.blog.api.repositories.UsersRepository;
import com.blog.api.security.principals.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final AccountsRepository accountsRepository;
    private final PostsRepository postsRepository;

    @Transactional
    public void createPost(PostRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Users user = accountsRepository.findByUsername(((UserPrincipal)authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> new RuntimeException("No user found with the given username"))
                .getUser();

        Posts post = Posts.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .author(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        postsRepository.save(post);
    }

    public PostsResponse getPost(long id){
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No post with such id found"));

        Users user = post.getAuthor();

        UserResponse userResponse = UserResponse.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .dateOfBirth(user.getDateOfBirth())
                .bio(user.getBio())
                .build();

        return PostsResponse.builder()
                .author(userResponse)
                .content(post.getContent())
                .title(post.getTitle())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
