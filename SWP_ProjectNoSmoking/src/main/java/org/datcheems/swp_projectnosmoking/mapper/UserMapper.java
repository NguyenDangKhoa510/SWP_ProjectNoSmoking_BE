package org.datcheems.swp_projectnosmoking.mapper;


import org.datcheems.swp_projectnosmoking.dto.request.RegisterRequest;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toUser(RegisterRequest request);

    @Mapping(target = "roles", expression = "java(mapRolesToStrings(user))")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    UserResponse toUserResponse(User user);

    default java.util.Set<String> mapRolesToStrings(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.toSet());
    }
}

