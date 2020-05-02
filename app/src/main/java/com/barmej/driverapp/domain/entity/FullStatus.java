package com.barmej.driverapp.domain.entity;

import java.io.Serializable;

public class FullStatus implements Serializable {
    private Driver driver;
    private Trip trip;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
