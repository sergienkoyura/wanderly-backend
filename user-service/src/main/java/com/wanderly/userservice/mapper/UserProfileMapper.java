package com.wanderly.userservice.mapper;

import com.wanderly.userservice.dto.UserProfileDto;
import com.wanderly.userservice.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileDto toUserProfileDto(UserProfile userProfile);
}
