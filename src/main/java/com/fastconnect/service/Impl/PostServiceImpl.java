package com.fastconnect.service.Impl;

import com.fastconnect.dto.CommentRequest;
import com.fastconnect.dto.CommentResponse;
import com.fastconnect.dto.PostRequest;
import com.fastconnect.dto.PostResponse;
import com.fastconnect.entity.Comment;
import com.fastconnect.entity.Post;
import com.fastconnect.entity.Reaction;
import com.fastconnect.entity.User;
import com.fastconnect.enums.EntityType;
import com.fastconnect.enums.NotificationType;
import com.fastconnect.enums.ReactionType;
import com.fastconnect.exception.PostNotFoundException;
import com.fastconnect.exception.UserNotFoundException;
import com.fastconnect.mapper.PostMapper;
import com.fastconnect.repository.CommentRepository;
import com.fastconnect.repository.PostRepository;
import com.fastconnect.repository.ReactionRepository;
import com.fastconnect.repository.UserRepository;
import com.fastconnect.service.NotificationService;
import com.fastconnect.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor // Uses final fields for injection
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final PostMapper postMapper;

    private final NotificationService notificationService;

    @Override
    public PostResponse createPost(PostRequest postRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Post post = postMapper.toEntity(postRequest);
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        return postMapper.toDTO(savedPost);
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        post.setContent(postRequest.getContent());
        post.setMediaUrl(postRequest.getMediaUrl());

        Post updatedPost = postRepository.save(post);
        return postMapper.toDTO(updatedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(postMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        return postMapper.toDTO(post);
    }

    @Override
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(postId);
        }
        postRepository.deleteById(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeedPosts(Long userId, Pageable pageable) {
        // TODO: Once Connection Module is ready, filter this by friends only.
        // For now, return Global Feed.
        return postRepository.findAll(pageable)
                .map(postMapper::toDTO);
    }


    @Override
    public PostResponse addComment(Long postId, Long userId, CommentRequest commentRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPost(post);
        comment.setUser(user);

        commentRepository.save(comment);
        if (!post.getUser().getUserId().equals(userId)) {
            String message = user.getProfile().getFullName() + " commented on your post.";
            notificationService.createNotification(
                    post.getUser().getUserId(), // Recipient
                    message,
                    NotificationType.COMMENT,
                    EntityType.POST,
                    post.getPostId()
            );
        }

        return postMapper.toDTO(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return post.getComments().stream()
                .map(postMapper::toCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateComment(Long commentId, Long userId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this comment");
        }

        comment.setContent(commentRequest.getContent());
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment not found with ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }


    @Override
    public void toggleReaction(Long postId, Long userId, ReactionType type) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Optional<Reaction> existingReaction = reactionRepository.findByPostAndUser(post, user);

        if (existingReaction.isPresent()) {
            reactionRepository.delete(existingReaction.get());
        } else {
            // Like
            Reaction reaction = new Reaction();
            reaction.setPost(post);
            reaction.setUser(user);
            reaction.setReactionType(type);
            reactionRepository.save(reaction);

            // ✅ TRIGGER NOTIFICATION (If liker is NOT the post owner)
            if (!post.getUser().getUserId().equals(userId)) {
                String message = user.getProfile().getFullName() + " reacted to your post.";
                notificationService.createNotification(
                        post.getUser().getUserId(), // Recipient
                        message,
                        NotificationType.REACTION,
                        EntityType.POST,
                        post.getPostId()
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getReactionsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return post.getReactions().stream()
                .map(reaction -> reaction.getUser().getProfile().getFullName())
                .collect(Collectors.toList());
    }
}