package org.datcheems.swp_projectnosmoking.mapper;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationBrief;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserNotificationResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "createdBy.fullName", target = "createdBy")
    NotificationResponse toDTO(Notification notification);

    @Mapping(source = "notification.title", target = "notificationTitle")
    @Mapping(source = "notification.content", target = "content")
    @Mapping(source = "deliveryStatus", target = "deliveryStatus")
    @Mapping(source = "sentAt", target = "sentAt")
    @Mapping(source = "personalizedReason", target = "personalizedReason")
    @Mapping(source = "isRead", target = "hasBeenRead")
    UserNotificationResponse toDTO(UserNotification userNotification);
    @Mapping(source = "notificationId", target = "id")
    @Mapping(source = "title", target = "title")



    NotificationBrief toBriefDTO(Notification notification);
}

