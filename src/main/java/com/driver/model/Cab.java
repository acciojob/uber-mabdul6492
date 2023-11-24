package com.driver.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Cab {

    @Id
    @GeneratedValue
    private int CabId;
    private int perKmRate;
    private boolean available;
    @OneToOne
    private Driver driver;

    public Cab() {
    }

    public Cab(int cabId, int perKmRate, boolean available, Driver driver) {
        CabId = cabId;
        this.perKmRate = perKmRate;
        this.available = available;
        this.driver = driver;
    }

    public int getCabId() {
        return CabId;
    }

    public void setCabId(int cabId) {
        CabId = cabId;
    }

    public int getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(int perKmRate) {
        this.perKmRate = perKmRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}