package com.wanderly.geoservice.mapper;

import com.wanderly.geoservice.dto.ARModelDto;
import com.wanderly.geoservice.entity.ARModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ARModelMapper {
    ARModelDto toDto(ARModel arModel);
    List<ARModelDto> toDtos(List<ARModel> arModelList);
}
