package com.example.identityservice.mapping;

import com.example.identityservice.dto.request.user.UserCreationRequest;
import com.example.identityservice.dto.response.user.UserResponse;
import com.example.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", qualifiedByName = "encryptPassword")
    User toUser(UserCreationRequest request);

    @Named("encryptPassword")
    default String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    UserResponse toUserResponse(User user);
}
