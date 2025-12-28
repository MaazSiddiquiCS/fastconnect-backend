package com.fastconnect.mapper;

import com.fastconnect.dto.SocietyMembershipRequest;
import com.fastconnect.dto.SocietyMembershipResponse;
import com.fastconnect.entity.SocietyMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SocietyMembershipMapper {

    @Mapping(source = "society.societyId", target = "societyId")
    @Mapping(source = "society.societyName", target = "societyName")
    @Mapping(source = "user.userId", target = "userId")
    SocietyMembershipResponse toDTO(SocietyMembership membership);

    List<SocietyMembershipResponse> toDTOList(List<SocietyMembership> memberships);

    // Note: Request to Entity mapping is tricky here as you need Society and User entities.
    // This method is generally done manually in the Service layer.
    // However, for basic mapping of fields that match:
    @Mapping(target = "membershipId", ignore = true)
    @Mapping(target = "society", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(source = "societyRole", target = "societyRole")
    SocietyMembership toEntity(SocietyMembershipRequest request);


    // Used primarily for role updates or status changes
    @Mapping(target = "membershipId", ignore = true)
    @Mapping(target = "society", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(@MappingTarget SocietyMembership membership, SocietyMembershipRequest request);
}