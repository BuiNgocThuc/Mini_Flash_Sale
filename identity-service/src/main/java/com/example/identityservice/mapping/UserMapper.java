package com.example.identityservice.mapping;

import com.example.identityservice.dto.request.user.UserCreationRequest;
import com.example.identityservice.dto.response.user.UserResponse;
import com.example.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

}
