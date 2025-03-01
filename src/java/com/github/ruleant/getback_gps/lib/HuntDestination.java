package com.github.ruleant.getback_gps.lib;

public class HuntDestination {

    private final String name;
    private final double latitude;
    private final double longitude;

    public HuntDestination(final String name, final double latitude, final double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
