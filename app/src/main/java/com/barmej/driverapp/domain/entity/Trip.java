package com.barmej.driverapp.domain.entity;

import java.io.Serializable;

public class Trip implements Serializable {
    private String id;
    private String status;
    private String driverId;
    private String riderId;

    private double pickUpLat;
    private double getPickUpLng;

    private double destinationLat;
    private double destinationLng;

    private double currentLat;
    private double getCurrentLng;

    public Trip(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public double getPickUpLat() {
        return pickUpLat;
    }

    public void setPickUpLat(double pickUpLat) {
        this.pickUpLat = pickUpLat;
    }

    public double getGetPickUpLng() {
        return getPickUpLng;
    }

    public void setGetPickUpLng(double getPickUpLng) {
        this.getPickUpLng = getPickUpLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getGetCurrentLng() {
        return getCurrentLng;
    }

    public void setGetCurrentLng(double getCurrentLng) {
        this.getCurrentLng = getCurrentLng;
    }
    public enum Status{
        GOING_TO_PICKUP,
        GOING_TO_DESTINATION,
        ARRIVED
    }
}
