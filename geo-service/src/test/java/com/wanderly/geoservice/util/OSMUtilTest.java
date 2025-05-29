package com.wanderly.geoservice.util;

import com.wanderly.common.exception.InternalException;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OSMUtilTest {

    @Test
    void prettify_ConvertsRawTextToReadableFormat() {
        String raw = "theme_park";
        String result = invokePrettify(raw);
        assertThat(result).isEqualTo("Theme Park");
    }

    @Test
    void generateRating_ReturnsValueBetween1And5() {
        for (int i = 0; i < 1000; i++) {
            double rating = invokeGenerateRating();
            assertThat(rating).isGreaterThanOrEqualTo(1.0).isLessThanOrEqualTo(5.0);
        }
    }

    @Test
    void fetchMarkers_ThrowsInternalException_OnFailure() {
        City city = new City();
        city.setId(UUID.randomUUID());
        city.setBoundingBox(Arrays.asList(0.0, 0.0, 0.0, null));

        assertThatThrownBy(() -> OSMUtil.fetchMarkers(city))
                .isInstanceOf(InternalException.class);
    }

    @Test
    void fetchMarkers_parsesValidMarker() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        String json = """
        {
          "elements": [
            {
              "type": "node",
              "id": 123,
              "lat": 50.45,
              "lon": 30.52,
              "tags": {
                "leisure": "park",
                "name": "Central Park"
              }
            }
          ]
        }
        """;

        when(mockResponse.body()).thenReturn(json);
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);

        // Inject the mock via reflection
        Field clientField = OSMUtil.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(null, mockClient);

        City city = new City();
        city.setId(UUID.randomUUID());
        city.setBoundingBox(List.of(50.44, 50.46, 30.51, 30.53)); // south, north, west, east

        List<Marker> markers = OSMUtil.fetchMarkers(city);

        assertThat(markers).hasSize(1);
        Marker marker = markers.getFirst();
        assertThat(marker.getName()).isEqualTo("Central Park");
        assertThat(marker.getTag()).isEqualTo(MarkerTag.PARK);
        assertThat(marker.getCategory()).isEqualTo(MarkerCategory.NATURE);
        assertThat(marker.getLatitude()).isEqualTo(50.45);
        assertThat(marker.getLongitude()).isEqualTo(30.52);
        assertThat(marker.getCityId()).isEqualTo(city.getId());
        assertThat(marker.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(marker.getRating()).isBetween(1.0, 5.0);
    }

    // Reflection helpers for private methods
    private String invokePrettify(String input) {
        try {
            var method = OSMUtil.class.getDeclaredMethod("prettify", String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double invokeGenerateRating() {
        try {
            var method = OSMUtil.class.getDeclaredMethod("generateRating");
            method.setAccessible(true);
            return (Double) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
