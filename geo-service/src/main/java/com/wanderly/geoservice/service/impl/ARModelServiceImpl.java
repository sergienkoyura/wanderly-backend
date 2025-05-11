package com.wanderly.geoservice.service.impl;

import com.wanderly.common.dto.UserARModelCompletionMessage;
import com.wanderly.geoservice.dto.ARModelDto;
import com.wanderly.geoservice.dto.ModelCompletionRequest;
import com.wanderly.geoservice.entity.ARModel;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.entity.UserPreferences;
import com.wanderly.geoservice.exception.ARModelNotFoundException;
import com.wanderly.geoservice.exception.ARModelVerificationFailureException;
import com.wanderly.geoservice.kafka.UserARModelCompletionProducer;
import com.wanderly.geoservice.mapper.ARModelMapper;
import com.wanderly.geoservice.repository.ARModelRepository;
import com.wanderly.geoservice.service.ARModelService;
import com.wanderly.geoservice.service.CityService;
import com.wanderly.geoservice.service.MarkerService;
import com.wanderly.geoservice.service.UserPreferencesService;
import com.wanderly.geoservice.util.ga.Router;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ARModelServiceImpl implements ARModelService {
    private final ARModelRepository arModelRepository;
    private final ARModelMapper arModelMapper;
    private final UserPreferencesService userPreferencesService;
    private final CityService cityService;
    private final MarkerService markerService;
    private final UserARModelCompletionProducer userARModelCompletionProducer;

    @Override
    public List<ARModelDto> findAllDtosByCityId(UUID userId, UUID cityId, Double userLatitude, Double userLongitude) {
        List<ARModel> models = arModelRepository.findAllByCityId(cityId);
        if (!models.isEmpty())
            return arModelMapper.toDtos(models);

        City city = cityService.findById(cityId); // get City for weight and center
        List<Marker> allMarkers = markerService.findAllByCityIdAndCategoryNature(cityId);
        UserPreferences preferences = userPreferencesService.findByUserId(userId);

        // Rate and sort markers
        List<Marker> sortedMarkers = allMarkers.stream()
                .sorted(Comparator.comparingDouble(m ->Router.calculateWeight(city, (Marker) m, preferences)).reversed()) // descending
                .toList();

        Set<String> usedSectors = new HashSet<>();
        int added = 0;

        if (userLatitude != null && userLongitude != null) {
            models.add(ARModel.builder()
                    .cityId(cityId)
                    .latitude(userLatitude)
                    .longitude(userLongitude)
                    .code(generateCode())
                    .build());

            usedSectors.add(Router.getSectorKey(userLatitude, userLongitude, 100));
            added++;
        }

        for (Marker marker : sortedMarkers) {
            if (added >= 5) break;

            String sectorKey = Router.getSectorKey(marker.getLatitude(), marker.getLongitude(), 100);
            if (usedSectors.contains(sectorKey)) continue;

            models.add(ARModel.builder()
                    .cityId(cityId)
                    .latitude(marker.getLatitude())
                    .longitude(marker.getLongitude())
                    .code(generateCode())
                    .build());

            usedSectors.add(sectorKey);
            added++;
        }

        // Save and return
        return arModelMapper.toDtos(arModelRepository.saveAll(models));
    }

    @Override
    public void verifyModel(UUID userId, ModelCompletionRequest request) {
        ARModel model = arModelRepository.findById(request.modelId())
                .orElseThrow(ARModelNotFoundException::new);

        if (!model.getCode().equals(request.code())) {
            throw new ARModelVerificationFailureException();
        }

        userARModelCompletionProducer.sendCompletionMessage(new UserARModelCompletionMessage(userId, request.modelId()));
    }

    private Integer generateCode() {
        return new Random().nextInt(8999) + 1000;
    }
}
