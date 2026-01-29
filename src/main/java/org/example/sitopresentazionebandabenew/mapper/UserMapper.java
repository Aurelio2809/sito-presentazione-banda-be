package org.example.sitopresentazionebandabenew.mapper;

import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;
import org.example.sitopresentazionebandabenew.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
