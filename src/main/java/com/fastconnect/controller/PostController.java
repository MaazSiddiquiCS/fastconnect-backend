package com.fastconnect.controller;

import com.fastconnect.dto.CommentRequest;
import com.fastconnect.dto.PostRequest;
import com.fastconnect.dto.PostResponse;
import com.fastconnect.enums.ReactionType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/{userId}")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest postRequest,
            @PathVariable Long userId) {
        PostResponse newPost = postService.createPost(postRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {
        Page<PostResponse> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{postId}/comments/{userId}")
    public ResponseEntity<PostResponse> addComment(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @Valid @RequestBody CommentRequest commentRequest) {
        PostResponse updatedPost = postService.addComment(postId, userId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedPost);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{postId}/react/{userId}")
    public ResponseEntity<Void> reactToPost(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestParam ReactionType type) {
        postService.toggleReaction(postId, userId, type);
        return ResponseEntity.ok().build();
    }
}