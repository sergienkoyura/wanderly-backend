package com.wanderly.geoservice.service;

import com.wanderly.geoservice.dto.CityDto;
import com.wanderly.geoservice.dto.UserPreferencesDto;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.UserPreferences;
import com.wanderly.geoservice.exception.UserPreferencesNotFound;
import com.wanderly.geoservice.mapper.UserPreferencesMapper;
import com.wanderly.geoservice.repository.UserPreferencesRepository;
import com.wanderly.geoservice.service.impl.UserPreferencesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
class UserPreferencesServiceTest {

    @Mock private UserPreferencesRepository repository;
    @Mock private UserPreferencesMapper mapper;
    @Mock private CityService cityService;
    @InjectMocks private UserPreferencesServiceImpl service;

    private UUID userId;
    private UserPreferences preferences;
    private UserPreferencesDto dto;
    private City city;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        preferences = new UserPreferences();
        preferences.setUserId(userId);

        dto = new UserPreferencesDto();
        dto.setCity(new CityDto());
        city = new City();
    }

    @Test
    void findDtoByUserId_ReturnsDto_WhenFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.of(preferences));
        when(mapper.toDto(preferences)).thenReturn(dto);

        UserPreferencesDto result = service.findDtoByUserId(userId);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void findDtoByUserId_Throws_WhenNotFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findDtoByUserId(userId))
                .isInstanceOf(UserPreferencesNotFound.class);
    }

    @Test
    void save_UpdatesExisting_WhenFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.of(preferences));
        when(cityService.findByOsmId(dto.getCity())).thenReturn(city);
        when(repository.save(preferences)).thenReturn(preferences);
        when(mapper.toDto(preferences)).thenReturn(dto);

        UserPreferencesDto result = service.save(userId, dto);

        assertThat(result).isEqualTo(dto);
        assertThat(preferences.getCity()).isEqualTo(city);
        verify(repository).save(preferences);
    }

    @Test
    void save_CreatesNew_WhenNotFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cityService.findByOsmId(dto.getCity())).thenReturn(city);
        when(repository.save(any())).thenReturn(preferences);
        when(mapper.toDto(preferences)).thenReturn(dto);

        UserPreferencesDto result = service.save(userId, dto);

        assertThat(result).isEqualTo(dto);
        verify(repository).save(any(UserPreferences.class));
    }

    @Test
    void findByUserId_ReturnsEntity_WhenFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.of(preferences));

        UserPreferences result = service.findByUserId(userId);

        assertThat(result).isEqualTo(preferences);
    }

    @Test
    void findByUserId_Throws_WhenNotFound() {
        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByUserId(userId))
                .isInstanceOf(UserPreferencesNotFound.class);
    }
}
