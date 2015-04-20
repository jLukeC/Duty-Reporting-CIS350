package com.example.swords.dutyreporting;

/**
 * Created by aolesky on 4/10/15.
 */
public class FenceLocation {
    private Double latitude;
    private Double longitude;
    private String name;

    public FenceLocation(String location) {
        String[] data = location.split(" ");

        this.name = data[0];
        this.latitude = Double.parseDouble(data[1]);
        this.longitude = Double.parseDouble(data[2]);
    }

    public FenceLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}
