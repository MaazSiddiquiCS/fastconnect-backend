package com.fastconnect.mapper;

import com.fastconnect.dto.SocietyRequest;
import com.fastconnect.dto.SocietyResponse;
import com.fastconnect.entity.Society;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SocietyMapper {

    // Helper method to compute the count of members from the entity set
    default int mapMemberCount(Society society) {
        return society.getSocietyMemberships() != null ? (int) society.getSocietyMemberships().stream().filter(m -> m.getActive()).count() : 0;
    }

    // Helper method to compute the count of followers from the entity set
    default int mapFollowerCount(Society society) {
        return society.getFollowers() != null ? society.getFollowers().size() : 0;
    }

    @Mapping(target = "memberCount", expression = "java(mapMemberCount(society))")
    @Mapping(target = "followerCount", expression = "java(mapFollowerCount(society))")
    SocietyResponse toDTO(Society society);

    List<SocietyResponse> toDTOList(List<Society> societies);

    // Request to Entity mapping (for creation)
    Society toEntity(SocietyRequest request);

    // Request to Entity mapping (for updates)
    @Mapping(target = "societyId", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "societyMemberships", ignore = true)
    @Mapping(target = "followers", ignore = true)
    void updateEntity(@MappingTarget Society society, SocietyRequest request);
}