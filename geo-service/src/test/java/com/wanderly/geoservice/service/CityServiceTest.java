package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.CityDto;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.exception.CityNotFoundException;
import com.wanderly.geoservice.mapper.CityMapper;
import com.wanderly.geoservice.repository.CityRepository;
import com.wanderly.geoservice.service.impl.CityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock private CityRepository cityRepository;
    @Mock private CityMapper cityMapper;
    @InjectMocks private CityServiceImpl cityService;

    private final UUID cityId = UUID.randomUUID();

    @Test
    void findById_ReturnsCity_WhenFound() {
        City city = new City();
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

        City result = cityService.findById(cityId);

        assertThat(result).isEqualTo(city);
        verify(cityRepository).findById(cityId);
    }

    @Test
    void findById_ThrowsException_WhenNotFound() {
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.findById(cityId))
                .isInstanceOf(CityNotFoundException.class);
    }

    @Test
    void findByOsmId_ReturnsCity_WhenExists() {
        CityDto cityDto = new CityDto();
        cityDto.setOsmId(123);
        City city = new City();

        when(cityRepository.findByOsmId(123)).thenReturn(Optional.of(city));

        City result = cityService.findByOsmId(cityDto);

        assertThat(result).isEqualTo(city);
        verify(cityRepository).findByOsmId(123);
        verify(cityRepository, never()).save(any());
    }

    @Test
    void findByOsmId_CreatesAndReturnsCity_WhenNotExists() {
        CityDto cityDto = new CityDto();
        cityDto.setOsmId(456);
        cityDto.setName("NewCity");

        City newCity = new City();

        when(cityRepository.findByOsmId(456)).thenReturn(Optional.empty());
        when(cityMapper.toCity(cityDto)).thenReturn(newCity);
        when(cityRepository.save(newCity)).thenReturn(newCity);

        City result = cityService.findByOsmId(cityDto);

        assertThat(result).isEqualTo(newCity);
        verify(cityRepository).save(newCity);
    }
}
