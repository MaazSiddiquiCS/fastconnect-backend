package com.fastconnect.mapper;

import com.fastconnect.dto.NotificationResponse;
import com.fastconnect.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "notificationId", target = "notificationId")
    NotificationResponse toDTO(Notification notification);
}