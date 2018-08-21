package com.edgaras.testapp;

public class FoundedPlace {
    private Double latitude;
    private Double longitude;
    private String name;
    private int lifespan;

    public FoundedPlace(Double latitude, Double longitude, String name, int lifespan) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.lifespan = lifespan;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public int getLifespan() {
        return lifespan;
    }
}
