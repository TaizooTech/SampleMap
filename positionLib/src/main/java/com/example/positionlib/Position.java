package com.example.positionlib;

public class Position {
    private double latitude;
    private double longitude;
    private float azimuth;

    public Position(double latitude, double longitude, float azimuth) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.azimuth = azimuth;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAzimuth() {
        return azimuth;
    }


}
