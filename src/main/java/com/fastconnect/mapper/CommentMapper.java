package com.fastconnect.mapper;

import com.fastconnect.dto.CommentResponse;
import com.fastconnect.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    // Maps fields from the Comment's nested User object
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.profile.fullName", target = "fullName")
    @Mapping(source = "user.profile.profilePic", target = "profilePic")
    // Maps the comment ID
    @Mapping(source = "commentId", target = "commentId")
    // Maps the post ID
    @Mapping(source = "post.postId", target = "postId")
    CommentResponse toDTO(Comment comment);

    List<CommentResponse> toDTOList(List<Comment> comments);

    // You would typically add a toEntity method here if you accept CommentRequest DTOs
}