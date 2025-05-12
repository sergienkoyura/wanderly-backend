package com.wanderly.authservice.mapper;

import com.wanderly.authservice.dto.response.UserDto;
import com.wanderly.authservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);
}
