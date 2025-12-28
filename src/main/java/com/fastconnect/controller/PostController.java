package com.fastconnect.controller;

import com.fastconnect.dto.CommentRequest;
import com.fastconnect.dto.CommentResponse;
import com.fastconnect.dto.PostRequest;
import com.fastconnect.dto.PostResponse;
import com.fastconnect.enums.ReactionType;
import com.fastconnect.security.CustomUserDetails;
import com.fastconnect.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Post, Comment & Reaction Management", description = "Endpoints for creating, managing, viewing, and interacting with user posts.")

public class PostController {

    @Autowired
    private PostService postService;


    @Operation(
            summary = "Create a new post.",
            description = "Allows an authenticated user to create a new post.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Post created successfully."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized: Missing or invalid token."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request: Invalid input data."
                    )
            }
    )
    @PostMapping("/")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PostResponse newPost = postService.createPost(postRequest, customUserDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
    }


    @Operation(
            summary = "Retrieve a paginated list of all posts.",
            description = "Retrieves all posts in the system. Requires authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of posts."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized."
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {
        Page<PostResponse> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }


    @Operation(
            summary = "Retrieve a post by ID.",
            description = "Returns the details of a specific post. Requires authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post found."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found."
                    )
            }
    )
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }


    @Operation(
            summary = "Delete a post.",
            description = "Allows the post owner or an ADMIN to delete a post.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Post deleted successfully (No Content)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not the post owner or Admin."
                    )
            }
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add a comment to a post.",
            description = "Adds a new comment by the authenticated user to the specified post.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment added successfully, returns the updated post."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized."
                    )
            }
    )
    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostResponse> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CommentRequest commentRequest) {
        PostResponse updatedPost = postService.addComment(postId, customUserDetails.getUserId(), commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedPost);
    }

    @Operation(
            summary = "Delete a comment.",
            description = "Allows the comment owner, the post owner, or an ADMIN to delete a comment.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Comment deleted successfully (No Content)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: Insufficient permissions."
                    )
            }
    )
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Toggle a reaction on a post.",
            description = "Adds or removes a reaction (e.g., LIKE, LOVE) by the authenticated user to the post. If the user already has a reaction of the same type, it is removed (toggled off).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reaction toggled successfully."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized."
                    )
            }
    )
    @PostMapping("/{postId}/react")
    public ResponseEntity<Void> reactToPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam ReactionType type) {
        postService.toggleReaction(postId, customUserDetails.getUserId(), type);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Update an existing post.",
            description = "Allows the post owner or an ADMIN to modify a post's content.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post updated successfully."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not the post owner or Admin."
                    )
            }
    )
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @Valid @RequestBody PostRequest postRequest) {
        PostResponse updatedPost = postService.updatePost(postId, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(
            summary = "Retrieve all comments for a post.",
            description = "Returns a list of all comments associated with the specified post ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved comments."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found."
                    )
            }
    )
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getCommentsByPostId(postId));
    }


    @Operation(
            summary = "Retrieve reactions for a post.",
            description = "Returns a list of reaction types and the users who applied them to the specified post.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved reactions."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found."
                    )
            }
    )
    @GetMapping("/{postId}/reactions")
    public ResponseEntity<List<String>> getReactions(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getReactionsByPostId(postId));
    }


    @Operation(
            summary = "Update an existing comment.",
            description = "Allows the comment owner or an ADMIN to edit the comment content.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment updated successfully."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden: User is not the comment owner or Admin."
                    )
            }
    )
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CommentRequest commentRequest) {
        postService.updateComment(commentId, customUserDetails.getUserId(), commentRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Retrieve the user's personalized feed.",
            description = "Returns a paginated list of posts relevant to the authenticated user (e.g., from followed users/societies).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the feed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized."
                    )
            }
    )
    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponse>> getFeed(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Pageable pageable) {
        Long userId = customUserDetails.getUserId();
        Page<PostResponse> feedPosts = postService.getFeedPosts(userId, pageable);
        return ResponseEntity.ok(feedPosts);
    }


}