package io.github.chrisvdalen.truckingmanager.model;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private double cash = 50000.0;
    private List<Truck> trucks = new ArrayList<>();
    private List<Driver> drivers = new ArrayList<>();
    private List<Job> activeJobs = new ArrayList<>();

    public Company() {
        trucks.add(new Truck("Starter Truck", 30));
    }

    public double getCash() { return cash; }
    public void addCash(double amount) { cash += amount; }
    public void subtractCash(double amount) { cash -= amount; }

    public List<Truck> getTrucks() { return trucks; }
    public List<Driver> getDrivers() { return drivers; }
    public List<Job> getActiveJobs() { return activeJobs; }
}
