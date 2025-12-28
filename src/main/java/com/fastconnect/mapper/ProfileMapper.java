package com.fastconnect.mapper;

import com.fastconnect.dto.ProfileRequest;
import com.fastconnect.dto.ProfileResponse;
import com.fastconnect.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(source = "user.userId", target = "userId")
    ProfileResponse toDTO(Profile profile);
    List<ProfileResponse> toDTOList(List<Profile> profiles);
    Profile toEntity(ProfileRequest request);
}
