package com.fosanzdev.trainingBrainAPI.controllers.posts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @PostMapping("/new")
    ResponseEntity<Map<String, Object>> createPost(){
        return ResponseEntity.ok(Map.of("message", "Post created"));
    }

    @PostMapping("/update/{postId}")
    ResponseEntity<Map<String, Object>> updatePost(){
        return ResponseEntity.ok(Map.of("message", "Post updated"));
    }

    @GetMapping("/{postId}")
    ResponseEntity<Map<String, Object>> getPost(){
        return ResponseEntity.ok(Map.of("message", "Post retrieved"));
    }

    @GetMapping("/from/{accountId}")
    ResponseEntity<Map<String, Object>> getPostsByAccount(){
        return ResponseEntity.ok(Map.of("message", "Posts retrieved"));
    }

    @DeleteMapping("/delete/{postId}")
    ResponseEntity<Map<String, Object>> deletePost(){
        return ResponseEntity.ok(Map.of("message", "Post deleted"));
    }



}
