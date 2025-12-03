package com.fastconnect.mapper;

import com.fastconnect.dto.UserRequest;
import com.fastconnect.dto.UserResponse;
import com.fastconnect.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDTO(User user);
    User toEntity(UserRequest userRequest);
    List<UserResponse> toDTOList(List<User> users);
}

