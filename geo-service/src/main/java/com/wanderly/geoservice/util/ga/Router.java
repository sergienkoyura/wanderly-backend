package com.wanderly.geoservice.util.ga;

import com.wanderly.geoservice.entity.City;
import com.wanderly.geoservice.entity.Marker;
import com.wanderly.geoservice.entity.UserPreferences;
import com.wanderly.geoservice.enums.ActivityType;
import com.wanderly.geoservice.enums.MarkerCategory;
import com.wanderly.geoservice.enums.MarkerTag;
import com.wanderly.geoservice.enums.TravelType;
import org.apache.catalina.User;

import java.util.*;
import java.util.stream.Collectors;

public class Router {

    public static ChromoRoute generateRoute(City city, List<Marker> markers, UserPreferences userPreferences) {
        List<GenMarker> genMarkers = markers.stream()
                .map(m -> new GenMarker(
                        m.getId(),
                        m.getLatitude(),
                        m.getLongitude(),
                        calculateWeight(city, m, userPreferences),
                        calculateTime(
                                m.getTag(),
                                userPreferences.getTimePerRoute(),
                                userPreferences.getTravelType()
                        ),
                        m.getCategory()
                ))
                .collect(Collectors.toList());

        List<ChromoRoute> population = generateInitialPopulation(genMarkers, userPreferences);

        return evolve(genMarkers, userPreferences, population);

//        return greedyGenerate(genMarkers, userPreferences);
    }


    private static double calculateWeight(City city, Marker marker, UserPreferences preferences) {
        ActivityType activityType = preferences.getActivityType();
        int timePerRoute = preferences.getTimePerRoute();
        MarkerCategory markerCategory = marker.getCategory();
        MarkerTag markerTag = marker.getTag();
        double rating = marker.getRating();

        // Activity type
        double activityScore = switch (activityType) {
            case OUTDOOR -> switch (markerCategory) {
                case LANDMARK -> switch (markerTag) {
                    case MUSEUM, GALLERY, CASTLE, RUINS, TEMPLE -> 5;
                    case MONUMENT, STATUE -> 4.5;
                    default -> 4;
                };
                case SCENIC -> 4;
                case NATURE -> 3.5;
                case FOOD -> 2.5;
                case ENTERTAINMENT -> switch (markerTag) {
                    case THEATRE, CINEMA -> 2;
                    default -> 1;
                };
            };

            case INDOOR -> switch (markerCategory) {
                case LANDMARK -> switch (markerTag) {
                    case MUSEUM, GALLERY -> 5;
                    case CHURCH, TEMPLE, MOSQUE -> 4.8;
                    case CASTLE, RUINS, STATUE -> 4.5;
                    default -> 4;
                };
                case ENTERTAINMENT -> switch (markerTag) {
                    case THEATRE, CINEMA -> 4;
                    default -> 3;
                };
                case FOOD -> 3.5;
                case NATURE -> 2;
                case SCENIC -> 1;
            };

            case COMBINED -> switch (markerCategory) {
//                case LANDMARK -> 4;
//                case FOOD -> 3;
                default -> 3;
            };
        };
        double normalizedActivity = (activityScore - 1) / 4;

        // Rating
        double normalizedRating = (rating - 1) / 4;
        double timePressureRatingMultiplier = switch (timePerRoute) {
            case 1, 2 -> 1.4;
            case 3, 4, 5 -> 1.2;
            case 6, 7 -> 1.1;
            default -> 1.0;
        };
        double boostedRating = Math.pow(normalizedRating, timePressureRatingMultiplier);

        double distanceFromCenter = haversine(city.getLatitude(), city.getLongitude(), marker.getLatitude(), marker.getLongitude());
        double normalizedCenter = 1 / Math.max(1, distanceFromCenter);
        double timePressureCenterMultiplier = switch (timePerRoute) {
            case 1, 2 -> 1.2;
            case 3, 4, 5 -> 1.1;
            default -> 1.0;
        };
        double boostedCenter = Math.pow(normalizedCenter, timePressureCenterMultiplier);

        return 0.2 * boostedCenter +
                0.5 * boostedRating +
                0.3 * normalizedActivity;
    }

    private static int calculateTime(MarkerTag tag, int timePerRoute, TravelType travelType) {
        int baseTime = switch (tag) {
            case PARK, GARDEN -> 30;
            case NATURE_RESERVE -> 45;

            case MUSEUM, GALLERY -> 45;
            case CASTLE -> 40;
            case MONUMENT, MEMORIAL, STATUE -> 10;
            case RUINS -> 25;
            case PLACE_OF_WORSHIP, CHURCH, MOSQUE, TEMPLE, SYNAGOGUE -> 20;

            case THEATRE, CINEMA -> 90;
            case LIBRARY -> 30;
            case ATTRACTION -> 40;
            case THEME_PARK -> 120;

            case CAFE -> 30;
            case RESTAURANT -> 45;
            case BAR, PUB -> 60;

            case VIEWPOINT -> 15;
            case TRAILHEAD -> 20;
        };

        // Time pressure
        // The less you have time, the faster you want to go through
        double timePressureMultiplier = 1;
        if (timePerRoute <= 2) {
            timePressureMultiplier *= 0.6;
        } else if (timePerRoute <= 5) {
            timePressureMultiplier *= 0.85;
        }

        // Travel type
        // You can stay more if on foot
        double travelTypeMultiplier = 1;
        if (travelType == TravelType.CAR) {
            travelTypeMultiplier *= 0.9;
        }

        return (int) Math.max(5, baseTime * timePressureMultiplier * travelTypeMultiplier);
    }

    private static List<ChromoRoute> generateInitialPopulation(List<GenMarker> markers, UserPreferences prefs) {
        // Population: 5% of all markers or 10
//        int populationSize = Math.max((int) Math.ceil(markers.size() * 0.05), 10);
//        int populationSize = (int) (markers.size() * 0.1);
        int populationSize = Math.max(20, (int) (Math.sqrt(markers.size()) * 5));
        System.out.println("population size: " + populationSize);

        List<ChromoRoute> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            ChromoRoute route = greedyGenerate(markers, prefs);
            population.add(route);
        }

        population.sort(Comparator.comparingDouble(ChromoRoute::getFitness).reversed());
        return population;
    }


    // greedy search for best options
    private static ChromoRoute greedyGenerate(List<GenMarker> allMarkers, UserPreferences prefs) {
        // todo try to combine elite pick + random (30/70)

        Set<GenMarker> used = new LinkedHashSet<>();

        List<GenMarker> bestMarkers = allMarkers.stream()
                .sorted(Comparator.comparingDouble(GenMarker::getWeight).reversed())
                .limit(Math.max(30, allMarkers.size() / 10)) // limit by
                .collect(Collectors.toList()); // mutable

        // Random start
        GenMarker current = bestMarkers.get(new Random().nextInt(bestMarkers.size()));
        used.add(current);

        int totalTime = current.getStayingTime();
        while (true) {
            int finalTotalTime = totalTime;
            GenMarker finalCurrent = current;

            Collections.shuffle(bestMarkers);

            GenMarker next = bestMarkers.stream()
                    .filter(m -> !used.contains(m)) // not used marker
                    .filter(m -> {
                        int travelTime = estimateTravelTime(finalCurrent, m, prefs.getTravelType());
                        return finalTotalTime + travelTime + m.getStayingTime() <= prefs.getTimePerRoute() * 60;
                    })
                    .findFirst()
                    .orElse(null);

            if (next == null) break;

            int travelTime = estimateTravelTime(current, next, prefs.getTravelType());
            totalTime += travelTime + next.getStayingTime();
            used.add(next);
            current = next;
        }

        double fitness = calculateFitness(used.stream().toList(), totalTime, prefs);
        return new ChromoRoute(used.stream().toList(), totalTime, fitness);
    }

    private static int estimateTravelTime(GenMarker from, GenMarker to, TravelType type) {
        double distanceKm = haversine(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());

        // haversine estimates a direct line between coordinates, fudge factor allows to cover the difference
        double fudgeFactor = switch (type) {
            case FOOT -> distanceKm <= 1 ? 1.6 : 1.4;
            case CAR -> distanceKm <= 2 ? 1.35 : 1.25;
        };
        double adjustedDistanceKm = distanceKm * fudgeFactor;
        double speedKmh = type == TravelType.FOOT ? 4.0 : 35.0;

        return (int) ((adjustedDistanceKm / speedKmh) * 60);
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }


    private static ChromoRoute evolve(List<GenMarker> allMarkers, UserPreferences prefs, List<ChromoRoute> population) {
        int generations = Math.min(allMarkers.size() * 10, 30000);
        double mutationRate = 0.1;

        double max = 0;
        int sizeToReach = population.stream()
                .map(el -> el.getMarkers().size())
                .max(Comparator.comparingInt(el -> el)).get();

        for (int gen = 0; gen < generations; gen++) {
            // select parents
            ChromoRoute parent1 = tournamentSelect(population);
            ChromoRoute parent2 = population.get(new Random().nextInt(population.size()));

            // crossover
            ChromoRoute child = crossover(parent1, parent2, prefs, sizeToReach);

            // mutation
            if (Math.random() < mutationRate) {
                child = mutate(child, allMarkers, prefs);
            }

            // local improvement
            ChromoRoute improved = localImprove(child, prefs);
            if (improved.getFitness() > child.getFitness()) {
                child = improved;
            }

            population.set(population.size() - 1, child);
            population.sort(Comparator.comparingDouble(ChromoRoute::getFitness).reversed());

            double fitness = population.getFirst().getFitness();
            if (fitness > max) {
                max = fitness;
                System.out.println("local winner: " + max + ", iteration: " + gen);
            }
        }

        ChromoRoute bestRoute = population.getFirst();
        bestRoute.getMarkers().removeIf(Objects::isNull);

        System.out.println("Best: " + bestRoute.getFitness());

        return bestRoute;
    }


    private static ChromoRoute tournamentSelect(List<ChromoRoute> population) {
        // best of random 20
        int toChoose = new Random().nextInt(population.size() - 20);
        return population.subList(toChoose, toChoose + 20).stream()
                .max(Comparator.comparingDouble(ChromoRoute::getFitness))
                .orElse(population.getFirst());
    }

    /**
     * Single-point crossover
     */
    private static ChromoRoute crossover(ChromoRoute p1, ChromoRoute p2, UserPreferences prefs, int sizeToReach) {
        List<GenMarker> result = new ArrayList<>();

        // enrich 1 route
        List<GenMarker> toAddRoute1 = new ArrayList<>();
        for (int i = 0; i < sizeToReach - p1.getMarkers().size(); i++) {
            toAddRoute1.add(null);
        }
        List<GenMarker> markersToSetRoute1 = new ArrayList<>();
        markersToSetRoute1.addAll(p1.getMarkers());
        markersToSetRoute1.addAll(toAddRoute1);
        p1.setMarkers(markersToSetRoute1);

        // enrich 2 route
        List<GenMarker> toAddRoute2 = new ArrayList<>();
        for (int i = 0; i < sizeToReach - p2.getMarkers().size(); i++) {
            toAddRoute2.add(null);
        }
        List<GenMarker> markersToSetRoute2 = new ArrayList<>();
        markersToSetRoute2.addAll(toAddRoute2);
        markersToSetRoute2.addAll(p2.getMarkers());
        p2.setMarkers(markersToSetRoute2);

        for (int i = 0; i < sizeToReach; i++) {
            if (new Random().nextInt() < 0.5) {
                if (result.contains(p1.getMarkers().get(i))) {
                    result.add(null);
                } else {
                    result.add(p1.getMarkers().get(i));
                }
            } else {
                if (result.contains(p2.getMarkers().get(i))) {
                    result.add(null);
                } else {
                    result.add(p2.getMarkers().get(i));
                }
            }
        }

        return rebuildRoute(result.stream().toList(), prefs);
    }

    /**
     * Helper method
     */
    private static ChromoRoute rebuildRoute(List<GenMarker> input, UserPreferences prefs) {
        List<GenMarker> route = new ArrayList<>();
        GenMarker current = input.getFirst();
        int totalTime = 0;
        int index = 1;
        while (current == null) {
            if (index == input.size()) {
                return new ChromoRoute(input, 0, 0);
            }
            current = input.get(index++);
        }
        totalTime = current.getStayingTime();
        route.add(current);

        for (int i = index; i < input.size(); i++) {
            GenMarker next = input.get(i);
            if (next == null) {
                route.add(null);
                continue;
            }

            int travel = estimateTravelTime(current, next, prefs.getTravelType());
            if (totalTime + travel + next.getStayingTime() <= prefs.getTimePerRoute() * 60) {
                route.add(next);
                totalTime += travel + next.getStayingTime();
                current = next;
            } else {
                route.add(null); // keep the slot
            }
        }

        double fitness = calculateFitness(route, totalTime, prefs);
        return new ChromoRoute(route, totalTime, fitness);
    }

    private static ChromoRoute mutate(ChromoRoute route, List<GenMarker> allMarkers, UserPreferences prefs) {
        List<GenMarker> current = new ArrayList<>(route.getMarkers());
        if (current.size() <= 2) return route;
        int indexToMutate = new Random().nextInt(current.size());

        Set<GenMarker> used = new HashSet<>(current);

        List<GenMarker> candidates = allMarkers.stream()
                .filter(m -> !used.contains(m))
                .sorted(Comparator.comparingDouble(GenMarker::getWeight).reversed())
                .toList();

        for (GenMarker candidate : candidates) {
            current.set(indexToMutate, candidate);
            ChromoRoute test = rebuildRoute(current, prefs);
            if (test.getTotalDuration() <= prefs.getTimePerRoute() * 60) {
                return test;
            }
        }

        return rebuildRoute(current, prefs);
    }

    private static ChromoRoute localImprove(ChromoRoute route, UserPreferences prefs) {
        List<GenMarker> markers = new ArrayList<>(route.getMarkers());
        int index1 = new Random().nextInt(markers.size());
        int index2 = new Random().nextInt(markers.size());

        Collections.swap(markers, index1, index2);
        return rebuildRoute(markers, prefs);
    }

    private static double calculateFitness(List<GenMarker> routeMarkers, int totalDuration, UserPreferences prefs) {
        double score = 0;
        int lastFoodIndex = 0;
        Set<String> visitedSectors = new HashSet<>();

        GenMarker previous = null;

        for (GenMarker current : routeMarkers) {
            if (current == null) continue;

            // sector loop penalty
            if (prefs.getActivityType() != ActivityType.INDOOR) {
                String sector = getSectorKey(current.getLatitude(), current.getLongitude(), prefs);
                if (visitedSectors.contains(sector)) {
                    if (prefs.getTimePerRoute() > 2 && prefs.getTimePerRoute() <= 5) {
                        score -= 1;
                    } else {
                        score -= 2;
                    }
                } else {
                    visitedSectors.add(sector);
                }
            }

            double currentScore = current.getWeight();

            if (previous != null) {
                double distanceKm = haversine(previous.getLatitude(), previous.getLongitude(), current.getLatitude(), current.getLongitude());

                if (prefs.getTravelType() == TravelType.FOOT) {
                    if (prefs.getTimePerRoute() <= 2) {
                        if (distanceKm <= 0.5) currentScore += 4;
                        else if (distanceKm <= 1) currentScore += 3;
                        else if (distanceKm <= 1.5) currentScore += 2;
                        else {
                            currentScore -= distanceKm + 2;
                        }
                    } else if (prefs.getTimePerRoute() <= 5) {
                        if (distanceKm <= 0.5) currentScore += 2.5;
                        else if (distanceKm <= 1) currentScore += 1.5;
                        else if (distanceKm <= 2) currentScore += 1;
                        else {
                            currentScore -= distanceKm + 1;
                        }
                    } else {
                        if (distanceKm <= 0.5) currentScore += 1.5;
                        else if (distanceKm <= 1) currentScore += 1;
                        else if (distanceKm <= 2) currentScore += 0;
                        else if (distanceKm <= 3) currentScore -= 2;
                        else {
                            currentScore -= distanceKm;
                        }
                    }
                } else {
                    if (prefs.getTimePerRoute() <= 5) {
                        if (distanceKm <= 3) currentScore += 1;
                        else {
                            currentScore -= 6;
                        }
                    } else {
                        if (distanceKm <= 1) currentScore += 1;
                        else if (distanceKm <= 4) currentScore += 0;
                        else if (distanceKm <= 6) currentScore -= 3;
                        else {
                            currentScore -= 6;
                        }
                    }
                }

                if (prefs.getActivityType() == ActivityType.INDOOR && distanceKm >= 2) {
                    currentScore -= distanceKm;
                }

                if (previous.getCategory() == MarkerCategory.FOOD && current.getCategory() == MarkerCategory.FOOD) {
                    score -= 3; // avoid consecutive food, can happen if the place is high-rated
                }
            }

            // Food frequency
            if (current.getCategory() == MarkerCategory.FOOD) {
                lastFoodIndex = 0;
            }
            else lastFoodIndex++;

            if (lastFoodIndex >= 3) // sooner or later will be found
                score -= 3;

            score += currentScore;
            previous = current;
        }

        // Overtime penalty
        int overtime = totalDuration - prefs.getTimePerRoute() * 60;
        if (overtime > 0) {
            score -= overtime * 0.2;
        }

        // Uptime penalty
        int uptime = prefs.getTimePerRoute() * 60 - totalDuration;
        if (uptime > 30) {
            score -= uptime * 0.2;
        }

        return Math.max(0, score); // never return negative
    }

    private static String getSectorKey(double lat, double lon, UserPreferences prefs) {
        // Approximate 0.3/1.1 km sector
        int multiplier = prefs.getTimePerRoute() <= 5 ? 200 : 100;
        int latSector = (int) (lat * multiplier);
        int lonSector = (int) (lon * multiplier);
        return latSector + "_" + lonSector;
    }

}
