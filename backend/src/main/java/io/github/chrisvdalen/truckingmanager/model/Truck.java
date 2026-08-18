package io.github.chrisvdalen.truckingmanager.model;

public class Truck {
    private static final java.util.concurrent.atomic.AtomicLong NEXT_ID = new java.util.concurrent.atomic.AtomicLong(1);
    private final long id;
    private final String name;
    private final double fuelConsumption;
    private double condition = 1.0; // 1.0 = new

    public Truck(String name, double fuelConsumption) {
        this.id = NEXT_ID.getAndIncrement();
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
