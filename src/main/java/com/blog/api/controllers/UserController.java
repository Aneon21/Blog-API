package com.blog.api.controllers;

import com.blog.api.mappers.responses.PostsResponse;
import com.blog.api.mappers.responses.UserResponse;
import com.blog.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable(name = "id") long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable(name = "id") long id){
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<Set<PostsResponse>> getAllUserPosts(@PathVariable(name = "id") long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllPosts(id));
    }
}
