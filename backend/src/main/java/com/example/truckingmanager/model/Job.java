package com.example.truckingmanager.model;

public class Job {
    private static long nextId = 1;
    private long id;
    private double distance;
    private double weight;
    private int durationDays;
    private double reward;

    private long assignedTruckId = -1;
    private int remainingDays;
    private double fuelCost;
    private double maintenanceCost;

    public Job(double distance, double weight, int durationDays, double reward) {
        this.id = nextId++;
        this.distance = distance;
        this.weight = weight;
        this.durationDays = durationDays;
        this.reward = reward;
        this.remainingDays = durationDays;
    }

    public long getId() { return id; }
    public double getDistance() { return distance; }
    public double getWeight() { return weight; }
    public int getDurationDays() { return durationDays; }
    public double getReward() { return reward; }

    public void assignTruck(long truckId, double fuelCost, double maintenanceCost) {
        this.assignedTruckId = truckId;
        this.fuelCost = fuelCost;
        this.maintenanceCost = maintenanceCost;
    }

    public boolean isAssigned() { return assignedTruckId != -1; }
    public int getRemainingDays() { return remainingDays; }
    public void progress() { if (remainingDays > 0) remainingDays--; }

    public boolean isComplete() { return remainingDays <= 0; }
    public double getFuelCost() { return fuelCost; }
    public double getMaintenanceCost() { return maintenanceCost; }
}
