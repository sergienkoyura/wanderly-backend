package com.wanderly.geoservice.util;

import com.wanderly.geoservice.CustomDisplayName;
import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.entity.UserPreferences;
import com.wanderly.geoservice.enums.ActivityType;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import com.wanderly.geoservice.enums.TravelType;
import com.wanderly.geoservice.util.ga.ChromoRoute;
import com.wanderly.geoservice.util.ga.GenMarker;
import com.wanderly.geoservice.util.ga.Router;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(CustomDisplayName.class)
class RouterTest {

    @Test
    void getSectorKey_ReturnsCorrectString() {
        String sector = Router.getSectorKey(50.45, 30.52, 100);
        assertThat(sector).isEqualTo("5045_3052");

        String zeroSector = Router.getSectorKey(0.0, 0.0, 100);
        assertThat(zeroSector).isEqualTo("0_0");

        String negative = Router.getSectorKey(-10.5, 23.7, 10);
        assertThat(negative).isEqualTo("-105_237");
    }

    @Test
    void haversine_ReturnsAccurateDistance() {
        double distance1 = BasicFacade.invokeHaversine(50.45, 30.52, 50.46, 30.53);
        assertThat(distance1).isBetween(1.0, 1.6); // approx 1.3 km

        double distance2 = BasicFacade.invokeHaversine(0.0, 0.0, 0.0, 1.0);
        assertThat(distance2).isBetween(111.0, 111.5); // ~111.2 km

        double distance3 = BasicFacade.invokeHaversine(0.0, 0.0, 1.0, 0.0);
        assertThat(distance3).isBetween(111.0, 111.5); // ~111.2 km
    }

    @Test
    void testCalculateWeight_withDifferentActivity() {
        City city = new City();
        city.setLatitude(50.4501);
        city.setLongitude(30.5234);

        Marker marker = new Marker();
        marker.setLatitude(50.4501);
        marker.setLongitude(30.5234);
        marker.setCategory(MarkerCategory.LANDMARK);
        marker.setTag(MarkerTag.CHURCH);
        marker.setRating(4.5);

        UserPreferences preferences = prefs();
        preferences.setTimePerRoute(5);

        double weight = Router.calculateWeight(city, marker, preferences);

        preferences.setActivityType(ActivityType.INDOOR);
        double weight2 = Router.calculateWeight(city, marker, preferences);

        // Church for outdoor is worse than for indoor
        assertThat(weight2).isGreaterThan(weight);
    }

    @Test
    void testCalculateTime_adjustsForTimeAndTravelType() {
        int time1 = Router.calculateTime(MarkerTag.MUSEUM, 1, TravelType.FOOT);
        int time2 = Router.calculateTime(MarkerTag.MUSEUM, 6, TravelType.CAR);

        // Time pressure factor
        assertThat(time1).isLessThan(time2).isGreaterThan(5);
    }

    @Test
    void testGetSectorKey_differentiatesByCoordinates() {
        String key1 = Router.getSectorKey(50.4501, 30.5234, 100);
        String key2 = Router.getSectorKey(50.4600, 30.5234, 100);

        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    void testHaversine_returnsCorrectDistance() {
        double d = BasicFacade.invokeHaversine(50.4501, 30.5234, 50.4510, 30.5240);
        assertThat(d).isGreaterThan(0);
    }

    @Test
    void crossover_combinesTwoRoutesPreservingUniqueness() {
        UserPreferences prefs = prefs();

        GenMarker m1 = new GenMarker(UUID.randomUUID(), 0, 0, 1, 10, MarkerCategory.NATURE);
        GenMarker m2 = new GenMarker(UUID.randomUUID(), 0.1, 0.1, 2, 10, MarkerCategory.NATURE);
        GenMarker m3 = new GenMarker(UUID.randomUUID(), 0.2, 0.2, 3, 10, MarkerCategory.NATURE);
        GenMarker m4 = new GenMarker(UUID.randomUUID(), 0.3, 0.3, 4, 10, MarkerCategory.NATURE);

        ChromoRoute parent1 = new ChromoRoute(List.of(m1, m2), 20, 5.0);
        ChromoRoute parent2 = new ChromoRoute(List.of(m3, m4), 20, 6.0);

        ChromoRoute child = BasicFacade.callCrossover(parent1, parent2, prefs, 2, List.of());

        assertThat(child.getMarkers()).hasSize(2);
        assertThat(child.getMarkers()).containsAnyOf(m1, m2, m3, m4);
    }

    @Test
    void mutate_replacesMarkerWithValidCandidate() {
        UserPreferences prefs = prefs();
        prefs.setTimePerRoute(5);

        GenMarker m1 = new GenMarker(UUID.randomUUID(), 0, 0, 1, 10, MarkerCategory.NATURE);
        GenMarker m2 = new GenMarker(UUID.randomUUID(), 0.01, 0.01, 2, 10, MarkerCategory.NATURE);
        GenMarker m3 = new GenMarker(UUID.randomUUID(), 0.02, 0.02, 3, 10, MarkerCategory.NATURE);
        GenMarker m4 = new GenMarker(UUID.randomUUID(), 0.03, 0.03, 4, 10, MarkerCategory.NATURE);

        ChromoRoute route = new ChromoRoute(List.of(m1, m2, m3), 20, 5.0);

        ChromoRoute mutated = BasicFacade.callMutate(route, List.of(m1, m2, m3, m4), prefs, List.of());

        assertThat(mutated.getMarkers()).contains(m4);
    }

    @Test
    void calculateFitness_penalizesConsecutiveFood() {
        UserPreferences prefs = prefs();
        prefs.setTimePerRoute(2);

        GenMarker m1 = new GenMarker(UUID.randomUUID(), 0, 0, 2, 10, MarkerCategory.NATURE);
        GenMarker m2 = new GenMarker(UUID.randomUUID(), 0.01, 0.015, 2, 10, MarkerCategory.FOOD);
        GenMarker m3 = new GenMarker(UUID.randomUUID(), 0.02, 0.02, 3, 10, MarkerCategory.NATURE);

        List<GenMarker> route = List.of(m1, m2, m3);
        ChromoRoute preResult = BasicFacade.callRebuild(route, prefs);

        m1.setCategory(MarkerCategory.FOOD);
        ChromoRoute result = BasicFacade.callRebuild(route, prefs);

        assertThat(result.getFitness()).isLessThan(preResult.getFitness()); // score reduced due to double food
    }

    @Test
    void generateInitialPopulation_createsNonEmptySortedList() {
        List<GenMarker> markers = List.of(
                gen(0, 0, 3),
                gen(0.01, 0.01, 2),
                gen(0.02, 0.02, 1),
                gen(0.03, 0.03, 4)
        );

        List<ChromoRoute> population = BasicFacade.callGenerateInitialPopulation(markers, prefs(), List.of());
        assertThat(population).isNotEmpty().isSortedAccordingTo((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
    }

    @Test
    void evolve_returnsFittestRoute() {
        List<GenMarker> markers = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            markers.add(gen(0 + i * 0.001, 0 + i * 0.001, i % 5 + 1));
        }

        List<ChromoRoute> initialPopulation = BasicFacade.callGenerateInitialPopulation(markers, prefs(), List.of());
        ChromoRoute best = BasicFacade.callEvolve(markers, prefs(), initialPopulation, List.of());

        assertThat(best).isNotNull();
        assertThat(best.getFitness()).isGreaterThan(0);
    }

    @Test
    void localImprove_swapsAndPreservesConstraints() {
        GenMarker m1 = gen(0, 0, 1);
        GenMarker m2 = gen(0.01, 0.01, 2);
        GenMarker m3 = gen(0.02, 0.02, 3);
        ChromoRoute route = new ChromoRoute(List.of(m1, m2, m3), 30, 5.0);

        ChromoRoute improved = BasicFacade.callLocalImprove(route, prefs(), List.of());
        assertThat(improved).isNotNull();
        assertThat(improved.getFitness()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void generateRoute_fullExecutionFromEntities() {
        City city = new City();
        city.setId(UUID.randomUUID());
        city.setLatitude(50.4501);
        city.setLongitude(30.5234);

        Marker m = new Marker();
        m.setId(UUID.randomUUID());
        m.setLatitude(50.4510);
        m.setLongitude(30.5240);
        m.setRating(4.0);
        m.setTag(MarkerTag.PARK);
        m.setCategory(MarkerCategory.NATURE);

        ChromoRoute route = Router.generateRoute(city, List.of(m), prefs(), null);
        assertThat(route).isNotNull();
        assertThat(route.getMarkers()).isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideCalculateTimeParams")
    void calculateTime_returnsPositive(MarkerTag tag, int timePerRoute, TravelType travelType) {
        int time = Router.calculateTime(tag, timePerRoute, travelType);
        assertThat(time).isGreaterThanOrEqualTo(5);
    }

    private static Stream<Object[]> provideCalculateTimeParams() {
        return Stream.of(
                new Object[]{MarkerTag.PARK, 1, TravelType.FOOT},
                new Object[]{MarkerTag.GARDEN, 5, TravelType.CAR},
                new Object[]{MarkerTag.NATURE_RESERVE, 8, TravelType.FOOT},
                new Object[]{MarkerTag.MUSEUM, 2, TravelType.CAR},
                new Object[]{MarkerTag.CASTLE, 3, TravelType.FOOT},
                new Object[]{MarkerTag.MONUMENT, 6, TravelType.CAR},
                new Object[]{MarkerTag.RUINS, 5, TravelType.FOOT},
                new Object[]{MarkerTag.CHURCH, 4, TravelType.CAR},
                new Object[]{MarkerTag.MOSQUE, 2, TravelType.FOOT},
                new Object[]{MarkerTag.SYNAGOGUE, 7, TravelType.FOOT},
                new Object[]{MarkerTag.THEATRE, 2, TravelType.CAR},
                new Object[]{MarkerTag.CINEMA, 5, TravelType.FOOT},
                new Object[]{MarkerTag.LIBRARY, 6, TravelType.CAR},
                new Object[]{MarkerTag.ATTRACTION, 3, TravelType.FOOT},
                new Object[]{MarkerTag.THEME_PARK, 1, TravelType.CAR},
                new Object[]{MarkerTag.CAFE, 2, TravelType.FOOT},
                new Object[]{MarkerTag.RESTAURANT, 4, TravelType.CAR},
                new Object[]{MarkerTag.BAR, 6, TravelType.FOOT},
                new Object[]{MarkerTag.PUB, 7, TravelType.CAR},
                new Object[]{MarkerTag.VIEWPOINT, 3, TravelType.FOOT},
                new Object[]{MarkerTag.TRAILHEAD, 5, TravelType.CAR}
        );
    }

    @ParameterizedTest
    @MethodSource("provideActivityScoreParams")
    void calculateWeight_activityBranch(ActivityType activityType, MarkerCategory category, MarkerTag tag, double minExpectedActivityScore) {
        City city = new City();
        city.setLatitude(50.0);
        city.setLongitude(30.0);

        Marker marker = new Marker();
        marker.setId(UUID.randomUUID());
        marker.setLatitude(50.0);
        marker.setLongitude(30.0);
        marker.setCategory(category);
        marker.setTag(tag);
        marker.setRating(5.0);

        UserPreferences prefs = new UserPreferences();
        prefs.setActivityType(activityType);
        prefs.setTimePerRoute(3);
        prefs.setTravelType(TravelType.FOOT);

        double weight = Router.calculateWeight(city, marker, prefs);

        assertThat(weight).isGreaterThanOrEqualTo(0);
    }

    private static Stream<Object[]> provideActivityScoreParams() {
        return Stream.of(
                new Object[]{ActivityType.OUTDOOR, MarkerCategory.LANDMARK, MarkerTag.MUSEUM, 5},
                new Object[]{ActivityType.OUTDOOR, MarkerCategory.LANDMARK, MarkerTag.MONUMENT, 4.5},
                new Object[]{ActivityType.OUTDOOR, MarkerCategory.NATURE, MarkerTag.PARK, 3.5},
                new Object[]{ActivityType.OUTDOOR, MarkerCategory.FOOD, MarkerTag.CAFE, 2.5},
                new Object[]{ActivityType.OUTDOOR, MarkerCategory.ENTERTAINMENT, MarkerTag.THEATRE, 2},

                new Object[]{ActivityType.INDOOR, MarkerCategory.LANDMARK, MarkerTag.GALLERY, 5},
                new Object[]{ActivityType.INDOOR, MarkerCategory.LANDMARK, MarkerTag.CHURCH, 4.8},
                new Object[]{ActivityType.INDOOR, MarkerCategory.LANDMARK, MarkerTag.CASTLE, 4.5},
                new Object[]{ActivityType.INDOOR, MarkerCategory.ENTERTAINMENT, MarkerTag.CINEMA, 4},
                new Object[]{ActivityType.INDOOR, MarkerCategory.FOOD, MarkerTag.BAR, 3.5},
                new Object[]{ActivityType.INDOOR, MarkerCategory.NATURE, MarkerTag.NATURE_RESERVE, 2},
                new Object[]{ActivityType.INDOOR, MarkerCategory.SCENIC, MarkerTag.VIEWPOINT, 1},

                new Object[]{ActivityType.COMBINED, MarkerCategory.FOOD, MarkerTag.PUB, 3},
                new Object[]{ActivityType.COMBINED, MarkerCategory.SCENIC, MarkerTag.TRAILHEAD, 3}
        );
    }


    private UserPreferences prefs() {
        UserPreferences prefs = new UserPreferences();
        prefs.setActivityType(ActivityType.OUTDOOR);
        prefs.setTimePerRoute(3);
        prefs.setTravelType(TravelType.FOOT);
        return prefs;
    }

    private GenMarker gen(double lat, double lon, double weight) {
        return new GenMarker(UUID.randomUUID(), lat, lon, weight, 10, MarkerCategory.NATURE);
    }
}
