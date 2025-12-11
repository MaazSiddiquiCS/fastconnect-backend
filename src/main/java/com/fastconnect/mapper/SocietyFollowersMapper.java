package com.fastconnect.mapper;

import com.fastconnect.dto.SocietyFollowersResponse;
import com.fastconnect.entity.SocietyFollowers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SocietyFollowersMapper {

    @Mapping(source = "society.societyId", target = "societyId")
    @Mapping(source = "society.societyName", target = "societyName")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    SocietyFollowersResponse toDTO(SocietyFollowers followers);

    List<SocietyFollowersResponse> toDTOList(List<SocietyFollowers> followers);
}