package com.wanderly.userservice.mapper;

import com.wanderly.userservice.dto.UserRouteCompletionDto;
import com.wanderly.userservice.entity.UserRouteCompletion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRouteCompletionMapper {
    UserRouteCompletion toEntity(UserRouteCompletionDto dto);

    UserRouteCompletionDto toDto(UserRouteCompletion foundCompletion);
}
