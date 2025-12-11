package com.fastconnect.controller;

import com.fastconnect.dto.CommentRequest;
import com.fastconnect.dto.CommentResponse;
import com.fastconnect.dto.PostRequest;
import com.fastconnect.dto.PostResponse;
import com.fastconnect.enums.ReactionType;
import com.fastconnect.security.CustomUserDetails;
import com.fastconnect.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PostResponse newPost = postService.createPost(postRequest, customUserDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
    }

    @GetMapping("/all")
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


    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostResponse> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CommentRequest commentRequest) {
        PostResponse updatedPost = postService.addComment(postId, customUserDetails.getUserId(), commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedPost);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{postId}/react")
    public ResponseEntity<Void> reactToPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam ReactionType type) {
        postService.toggleReaction(postId, customUserDetails.getUserId(), type);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @Valid @RequestBody PostRequest postRequest) {
        PostResponse updatedPost = postService.updatePost(postId, postRequest);
        return ResponseEntity.ok(updatedPost);
    }
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getCommentsByPostId(postId));
    }

    @GetMapping("/{postId}/reactions")
    public ResponseEntity<List<String>> getReactions(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getReactionsByPostId(postId));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CommentRequest commentRequest) {
        postService.updateComment(commentId, customUserDetails.getUserId(), commentRequest);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getFeed(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable) {
        Long userId = customUserDetails.getUserId();
        Page<PostResponse> feedPosts = postService.getFeedPosts(userId, pageable);
        return ResponseEntity.ok(feedPosts);
    }


}