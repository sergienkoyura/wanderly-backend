package com.wanderly.geoservice.util;

import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class OSMUtil {

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Marker> fetchMarkers(City city) {
        try {

            double south = city.getBoundingBox().get(0);
            double north = city.getBoundingBox().get(1);
            double west = city.getBoundingBox().get(2);
            double east = city.getBoundingBox().get(3);

            String bbox = String.format(Locale.US, "(%.7f,%.7f,%.7f,%.7f)", south, west, north, east);
            String query = """
            [out:json][timeout:60];
            (
              node["leisure"~"park|garden|nature_reserve"]%s;
              way["leisure"~"park|garden|nature_reserve"]%s;
              relation["leisure"~"park|garden|nature_reserve"]%s;
            
              node["historic"~"monument|memorial|castle|ruins"]%s;
              way["historic"~"monument|memorial|castle|ruins"]%s;
              relation["historic"~"monument|memorial|castle|ruins"]%s;
              node["man_made"="statue"]%s;
              way["man_made"="statue"]%s;
              relation["man_made"="statue"]%s;
              node["tourism"~"museum|gallery"]%s;
              way["tourism"~"museum|gallery"]%s;
              relation["tourism"~"museum|gallery"]%s;
              node["amenity"="place_of_worship"]%s;
              way["amenity"="place_of_worship"]%s;
              relation["amenity"="place_of_worship"]%s;
              node["building"~"church|mosque|synagogue|temple"]%s;
              way["building"~"church|mosque|synagogue|temple"]%s;
              relation["building"~"church|mosque|synagogue|temple"]%s;
            
              node["tourism"~"attraction|theme_park"]%s;
              node["amenity"~"theatre|cinema|library"]%s;
              way["amenity"~"theatre|cinema|library"]%s;
              relation["amenity"~"theatre|cinema|library"]%s;
            
              node["amenity"~"cafe|restaurant|bar|pub"]%s;
            
              node["tourism"="viewpoint"]%s;
              node["highway"="trailhead"]%s;
            );
            out center;
            """.formatted(bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox,
                    bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox, bbox);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OVERPASS_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("data=" + query))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body()).get("elements");

            List<Marker> markers = new ArrayList<>();
            for (JsonNode element : root) {
                JsonNode tags = element.get("tags");
                if (tags == null) continue;

                Map.Entry<String, String> tagEntry = null;
                for (Iterator<Map.Entry<String, JsonNode>> it = tags.fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    String key = entry.getKey();
                    String value = entry.getValue().asText();

                    if (OSMMarkerMapper.getMarkerTag(key, value) != null) {
                        tagEntry = Map.entry(key, value);
                        break;
                    }
                }

                if (tagEntry == null) continue; // skip if no valid tag found

                String tagKey = tagEntry.getKey();
                String tagValue = tagEntry.getValue();

                MarkerTag tag = OSMMarkerMapper.getMarkerTag(tagKey, tagValue);
                MarkerCategory category = OSMMarkerMapper.getCategory(tag);
                String name = tags.has("name") ? tags.get("name").asText() : prettify(tagValue);

                double lat = element.has("lat") ? element.get("lat").asDouble() : element.get("center").get("lat").asDouble();
                double lon = element.has("lon") ? element.get("lon").asDouble() : element.get("center").get("lon").asDouble();

                markers.add(new Marker(
                        null,
                        lat,
                        lon,
                        name,
                        tag,
                        category,
                        city.getId(),
                        generateRating(),
                        LocalDateTime.now()
                ));
            }

            return markers;
        } catch (Exception e) {
            log.error("Exception while parsing markers: {}", e.getMessage());
            throw new RuntimeException("Server error occurred. Try again later");
        }
    }

    private static String prettify(String raw) {
        // Replace underscores, dashes, etc. with spaces
        String cleaned = raw.replaceAll("[_\\-]+", " ").replaceAll("[^\\w\\s]", "");
        // Capitalize each word
        return Arrays.stream(cleaned.split("\\s+"))
                .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private static Double generateRating() {
        return new Random().nextDouble() * 4 + 1;
    }
} 
