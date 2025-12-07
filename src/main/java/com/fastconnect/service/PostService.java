package com.fastconnect.service;

import com.fastconnect.dto.CommentRequest;
import com.fastconnect.dto.PostRequest;
import com.fastconnect.dto.PostResponse;
import com.fastconnect.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostResponse createPost(PostRequest postRequest, Long userId);
    Page<PostResponse> getAllPosts(Pageable pageable);
    PostResponse getPostById(Long postId);
    void deletePost(Long postId);

    PostResponse addComment(Long postId, Long userId, CommentRequest commentRequest);
    void deleteComment(Long commentId);

    void toggleReaction(Long postId, Long userId, ReactionType type);
}