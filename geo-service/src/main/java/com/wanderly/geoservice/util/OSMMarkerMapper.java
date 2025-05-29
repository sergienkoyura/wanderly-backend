package com.wanderly.geoservice.util;

import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;

public class OSMMarkerMapper {
    public static MarkerTag getMarkerTag(String key, String value) {
        return switch (key + "=" + value) {
            case "leisure=park" -> MarkerTag.PARK;
            case "leisure=garden" -> MarkerTag.GARDEN;
            case "leisure=nature_reserve" -> MarkerTag.NATURE_RESERVE;
            case "historic=castle" -> MarkerTag.CASTLE;
            case "historic=monument" -> MarkerTag.MONUMENT;
            case "historic=memorial" -> MarkerTag.MEMORIAL;
            case "historic=ruins" -> MarkerTag.RUINS;
            case "man_made=statue" -> MarkerTag.STATUE;
            case "tourism=museum" -> MarkerTag.MUSEUM;
            case "tourism=gallery" -> MarkerTag.GALLERY;
            case "amenity=place_of_worship" -> MarkerTag.PLACE_OF_WORSHIP;
            case "building=church" -> MarkerTag.CHURCH;
            case "building=mosque" -> MarkerTag.MOSQUE;
            case "building=synagogue" -> MarkerTag.SYNAGOGUE;
            case "building=temple" -> MarkerTag.TEMPLE;
            case "amenity=cafe" -> MarkerTag.CAFE;
            case "amenity=restaurant" -> MarkerTag.RESTAURANT;
            case "amenity=bar" -> MarkerTag.BAR;
            case "amenity=pub" -> MarkerTag.PUB;
            case "tourism=attraction" -> MarkerTag.ATTRACTION;
            case "tourism=theme_park" -> MarkerTag.THEME_PARK;
            case "amenity=library" -> MarkerTag.LIBRARY;
            case "amenity=theatre" -> MarkerTag.THEATRE;
            case "amenity=cinema" -> MarkerTag.CINEMA;
            case "tourism=viewpoint" -> MarkerTag.VIEWPOINT;
            case "highway=trailhead" -> MarkerTag.TRAILHEAD;
            default -> null;
        };
    }

    public static MarkerCategory getCategory(MarkerTag tag) {
        return switch (tag) {
            case PARK, GARDEN, NATURE_RESERVE -> MarkerCategory.NATURE;
            case CASTLE, MONUMENT, MEMORIAL, RUINS, STATUE, MUSEUM, GALLERY,
                 PLACE_OF_WORSHIP, CHURCH, MOSQUE, TEMPLE, SYNAGOGUE -> MarkerCategory.LANDMARK;
            case CAFE, RESTAURANT, BAR, PUB -> MarkerCategory.FOOD;
            case ATTRACTION, THEME_PARK, CINEMA, THEATRE, LIBRARY -> MarkerCategory.ENTERTAINMENT;
            case VIEWPOINT, TRAILHEAD -> MarkerCategory.SCENIC;
        };
    }
}
