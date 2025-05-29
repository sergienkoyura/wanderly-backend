package com.wanderly.geoservice.util;

import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OSMMarkerMapperTest {

    @Test
    void getMarkerTag_ReturnsCorrectTag() {
        assertThat(OSMMarkerMapper.getMarkerTag("leisure", "park")).isEqualTo(MarkerTag.PARK);
        assertThat(OSMMarkerMapper.getMarkerTag("leisure", "garden")).isEqualTo(MarkerTag.GARDEN);
        assertThat(OSMMarkerMapper.getMarkerTag("leisure", "nature_reserve")).isEqualTo(MarkerTag.NATURE_RESERVE);

        assertThat(OSMMarkerMapper.getMarkerTag("historic", "castle")).isEqualTo(MarkerTag.CASTLE);
        assertThat(OSMMarkerMapper.getMarkerTag("historic", "monument")).isEqualTo(MarkerTag.MONUMENT);
        assertThat(OSMMarkerMapper.getMarkerTag("historic", "memorial")).isEqualTo(MarkerTag.MEMORIAL);
        assertThat(OSMMarkerMapper.getMarkerTag("historic", "ruins")).isEqualTo(MarkerTag.RUINS);

        assertThat(OSMMarkerMapper.getMarkerTag("man_made", "statue")).isEqualTo(MarkerTag.STATUE);

        assertThat(OSMMarkerMapper.getMarkerTag("tourism", "museum")).isEqualTo(MarkerTag.MUSEUM);
        assertThat(OSMMarkerMapper.getMarkerTag("tourism", "gallery")).isEqualTo(MarkerTag.GALLERY);
        assertThat(OSMMarkerMapper.getMarkerTag("tourism", "attraction")).isEqualTo(MarkerTag.ATTRACTION);
        assertThat(OSMMarkerMapper.getMarkerTag("tourism", "theme_park")).isEqualTo(MarkerTag.THEME_PARK);
        assertThat(OSMMarkerMapper.getMarkerTag("tourism", "viewpoint")).isEqualTo(MarkerTag.VIEWPOINT);

        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "place_of_worship")).isEqualTo(MarkerTag.PLACE_OF_WORSHIP);
        assertThat(OSMMarkerMapper.getMarkerTag("building", "church")).isEqualTo(MarkerTag.CHURCH);
        assertThat(OSMMarkerMapper.getMarkerTag("building", "mosque")).isEqualTo(MarkerTag.MOSQUE);
        assertThat(OSMMarkerMapper.getMarkerTag("building", "synagogue")).isEqualTo(MarkerTag.SYNAGOGUE);
        assertThat(OSMMarkerMapper.getMarkerTag("building", "temple")).isEqualTo(MarkerTag.TEMPLE);

        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "cafe")).isEqualTo(MarkerTag.CAFE);
        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "restaurant")).isEqualTo(MarkerTag.RESTAURANT);
        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "bar")).isEqualTo(MarkerTag.BAR);
        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "pub")).isEqualTo(MarkerTag.PUB);

        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "library")).isEqualTo(MarkerTag.LIBRARY);
        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "theatre")).isEqualTo(MarkerTag.THEATRE);
        assertThat(OSMMarkerMapper.getMarkerTag("amenity", "cinema")).isEqualTo(MarkerTag.CINEMA);

        assertThat(OSMMarkerMapper.getMarkerTag("highway", "trailhead")).isEqualTo(MarkerTag.TRAILHEAD);
    }

    @Test
    void getMarkerTag_ReturnsNull_WhenUnknown() {
        assertThat(OSMMarkerMapper.getMarkerTag("unknown", "value")).isNull();
        assertThat(OSMMarkerMapper.getMarkerTag("", "")).isNull();
    }

    @Test
    void getCategory_ReturnsCorrectCategory() {
        assertThat(OSMMarkerMapper.getCategory(MarkerTag.GARDEN)).isEqualTo(MarkerCategory.NATURE);
        assertThat(OSMMarkerMapper.getCategory(MarkerTag.MUSEUM)).isEqualTo(MarkerCategory.LANDMARK);
        assertThat(OSMMarkerMapper.getCategory(MarkerTag.CAFE)).isEqualTo(MarkerCategory.FOOD);
        assertThat(OSMMarkerMapper.getCategory(MarkerTag.CINEMA)).isEqualTo(MarkerCategory.ENTERTAINMENT);
        assertThat(OSMMarkerMapper.getCategory(MarkerTag.TRAILHEAD)).isEqualTo(MarkerCategory.SCENIC);
    }
}
