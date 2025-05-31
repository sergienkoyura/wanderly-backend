package com.wanderly.geoservice;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class CustomDisplayName extends DisplayNameGenerator.Standard {
    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return convertCamelCaseToSpaces(testMethod.getName());
    }

    private String convertCamelCaseToSpaces(String name) {
        return name.replaceAll("([a-z])([A-Z])", "$1 $2").replaceAll("_", " ");
    }
}