package org.example.sitopresentazionebandabenew.mapper;

import java.util.List;
import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;
import org.example.sitopresentazionebandabenew.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(source = "createdAt", target = "createdAt")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
