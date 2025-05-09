package com.wanderly.geoservice.util.ga;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChromoRoute {
    private List<GenMarker> markers;
    private int totalDuration;
    private double fitness;
}
