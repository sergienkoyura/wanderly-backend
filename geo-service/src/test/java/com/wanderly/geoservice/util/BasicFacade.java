package com.wanderly.geoservice.util;

import com.wanderly.geoservice.entity.UserPreferences;
import com.wanderly.geoservice.util.ga.ChromoRoute;
import com.wanderly.geoservice.util.ga.GenMarker;
import com.wanderly.geoservice.util.ga.Router;

import java.lang.reflect.Method;
import java.util.List;

public class BasicFacade {
    public static double invokeHaversine(double lat1, double lon1, double lat2, double lon2) {
        try {
            var haversine = Router.class.getDeclaredMethod("haversine", double.class, double.class, double.class, double.class);
            haversine.setAccessible(true);
            return (double) haversine.invoke(null, lat1, lon1, lat2, lon2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

     public static ChromoRoute callCrossover(ChromoRoute p1, ChromoRoute p2, UserPreferences prefs, int size, List<GenMarker> prefix) {
        try {
            Method method = Router.class.getDeclaredMethod("crossover", ChromoRoute.class, ChromoRoute.class, UserPreferences.class, int.class, List.class);
            method.setAccessible(true);
            return (ChromoRoute) method.invoke(null, p1, p2, prefs, size, prefix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChromoRoute callMutate(ChromoRoute route, List<GenMarker> all, UserPreferences prefs, List<GenMarker> prefix) {
        try {
            Method method = Router.class.getDeclaredMethod("mutate", ChromoRoute.class, List.class, UserPreferences.class, List.class);
            method.setAccessible(true);
            return (ChromoRoute) method.invoke(null, route, all, prefs, prefix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChromoRoute callRebuild(List<GenMarker> markers, UserPreferences prefs) {
        try {
            Method method = Router.class.getDeclaredMethod("rebuildRoute", List.class, UserPreferences.class);
            method.setAccessible(true);
            return (ChromoRoute) method.invoke(null, markers, prefs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ChromoRoute> callGenerateInitialPopulation(List<GenMarker> markers, UserPreferences prefs, List<GenMarker> prefix) {
        try {
            Method method = Router.class.getDeclaredMethod("generateInitialPopulation", List.class, UserPreferences.class, List.class);
            method.setAccessible(true);
            return (List<ChromoRoute>) method.invoke(null, markers, prefs, prefix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChromoRoute callGreedyGenerate(List<GenMarker> markers, UserPreferences prefs, List<GenMarker> prefix) {
        try {
            Method method = Router.class.getDeclaredMethod("greedyGenerate", List.class, UserPreferences.class, List.class);
            method.setAccessible(true);
            return (ChromoRoute) method.invoke(null, markers, prefs, prefix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChromoRoute callEvolve(List<GenMarker> markers, UserPreferences prefs, List<ChromoRoute> population, List<GenMarker> prefix) {
        try {
            Method method = Router.class.getDeclaredMethod("evolve", List.class, UserPreferences.class, List.class, List.class);
            method.setAccessible(true);
            return (ChromoRoute) method.invoke(null, markers, prefs, population, prefix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ChromoRoute callLocalImprove(ChromoRoute route, UserPreferences prefs, List<GenMarker> prefix) {
        try {
            Method method = Router.class.getDeclaredMethod("localImprove", ChromoRoute.class, UserPreferences.class, List.class);
            method.setAccessible(true);
            return (ChromoRoute) method.invoke(null, route, prefs, prefix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
