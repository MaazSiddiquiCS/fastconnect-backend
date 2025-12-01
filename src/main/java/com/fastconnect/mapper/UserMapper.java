package com.fastconnect.mapper;

import com.fastconnect.dto.UserResponse;
import com.fastconnect.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDTO(User user);
    User toEntity(UserResponse userResponse);
    List<UserResponse> toDTOList(List<User> users);
}

