package com.example.truckingmanager.model;

public class Truck {
    private static long nextId = 1;
    private long id;
    private String name;
    private double fuelConsumption; // liters per 100km
    private double condition = 1.0; // 1.0 = new

    public Truck(String name, double fuelConsumption) {
        this.id = nextId++;
        this.name = name;
        this.fuelConsumption = fuelConsumption;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getCondition() {
        return condition;
    }

    public void degrade(double amount) {
        this.condition = Math.max(0, this.condition - amount);
    }
}
