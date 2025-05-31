package com.wanderly.geoservice.util.ga;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChromoRoute {
    private List<GenMarker> markers;
    private int totalDuration;
    private double fitness;
}
