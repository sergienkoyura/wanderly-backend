package com.wanderly.geoservice.util.ga;

import com.wanderly.geoservice.enums.MarkerCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenMarker {
    private UUID id;
    private double latitude;
    private double longitude;
    private double weight;
    private int stayingTime; // minutes
    private MarkerCategory category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenMarker genMarker = (GenMarker) o;
        return Objects.equals(id, genMarker.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
