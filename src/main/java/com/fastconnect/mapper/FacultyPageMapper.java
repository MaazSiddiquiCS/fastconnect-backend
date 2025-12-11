package com.fastconnect.mapper;

import com.fastconnect.dto.FacultyPageRequest;
import com.fastconnect.dto.FacultyPageResponse;
import com.fastconnect.entity.FacultyPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FacultyPageMapper {

    @Mapping(source = "user.userId", target = "userId")
    FacultyPageResponse toDTO(FacultyPage facultyPage);

    @Mapping(target = "facultyId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    FacultyPage toEntity(FacultyPageRequest request);

    @Mapping(target = "facultyId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    void updateEntity(@MappingTarget FacultyPage facultyPage, FacultyPageRequest request);
}