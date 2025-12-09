package com.fastconnect.mapper;

import com.fastconnect.dto.ConnectionResponse;
import com.fastconnect.entity.Connection;
import com.fastconnect.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConnectionMapper {

    @Mappings({
            @Mapping(source = "connection.connectionId", target = "connectionId"),
            @Mapping(source = "connection.connectedAt", target = "connectedAt"),
            @Mapping(source = "connectedUser.userId", target = "userId"),
            @Mapping(source = "connectedUser.profile.fullName", target = "userFullName"),
            @Mapping(source = "connectedUser.profile.profilePic", target = "userProfilePic")
    })
    ConnectionResponse mapWithConnectedUser(Connection connection, User connectedUser);

    List<ConnectionResponse> toConnectionResponseDTOList(List<Connection> connections);
}