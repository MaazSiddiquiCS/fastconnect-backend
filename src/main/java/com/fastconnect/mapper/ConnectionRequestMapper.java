package com.fastconnect.mapper;

import com.fastconnect.dto.ConnectionRequestActionDTO;
import com.fastconnect.dto.ConnectionRequestDetails;
import com.fastconnect.entity.ConnectionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConnectionRequestMapper {
    @Mappings({
            // Map the ConnectionRequest's ID and dates directly
            @Mapping(source = "connectionRequestId", target = "connectionRequestId"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "respondedAt", target = "respondedAt"),

            // Map the User IDs from the nested entity fields
            @Mapping(source = "sender.userId", target = "senderId"),
            @Mapping(source = "receiver.userId", target = "receiverId"),

            // Example of mapping nested user details for the UI (Crucial for display)
            @Mapping(source = "sender.profile.fullName", target = "senderFullName"),
            @Mapping(source = "receiver.profile.fullName", target = "receiverFullName")
    })
    ConnectionRequestDetails toDetailsDTO(ConnectionRequest connectionRequest);


    @Mapping(target = "respondedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromActionDTO(ConnectionRequestActionDTO actionDTO, @MappingTarget ConnectionRequest connectionRequest);}
