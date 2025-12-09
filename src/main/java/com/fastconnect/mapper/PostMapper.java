package com.fastconnect.mapper;

import com.fastconnect.dto.PostRequest;
import com.fastconnect.dto.PostResponse;
import com.fastconnect.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.profile.fullName", target = "fullName")
    @Mapping(source = "user.profile.profilePic", target = "profilePic")
    @Mapping(target = "likeCount", expression = "java(post.getReactions() != null ? post.getReactions().size() : 0)")
    @Mapping(target = "commentCount", expression = "java(post.getComments() != null ? post.getComments().size() : 0)")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.profile.fullName", target = "fullName")
    @Mapping(source = "user.profile.profilePic", target = "profilePic")
    com.fastconnect.dto.CommentResponse toCommentDTO(com.fastconnect.entity.Comment comment);
    PostResponse toDTO(Post post);

    Post toEntity(PostRequest postRequest);
    List<PostResponse> toDTOList(List<Post> posts);
}