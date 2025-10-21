package com.blog.api.controllers;

import com.blog.api.mappers.requests.PostRequest;
import com.blog.api.mappers.responses.PostsResponse;
import com.blog.api.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @PostMapping("")
    public ResponseEntity<String> createPost(@RequestBody PostRequest request){
        postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("New post created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostsResponse> getPostById(@PathVariable(name = "id") long id){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(id));
    }
}
