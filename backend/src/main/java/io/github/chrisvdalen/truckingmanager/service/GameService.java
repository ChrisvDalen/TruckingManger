package io.github.chrisvdalen.truckingmanager.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.random.RandomGenerator;

import org.springframework.stereotype.Service;

import io.github.chrisvdalen.truckingmanager.model.Company;
import io.github.chrisvdalen.truckingmanager.model.Driver;
import io.github.chrisvdalen.truckingmanager.model.Job;
import io.github.chrisvdalen.truckingmanager.model.Truck;

@Service
public class GameService {
    private final Company company = new Company();
    private final List<Job> availableJobs = new ArrayList<>();
    private final RandomGenerator random = RandomGenerator.getDefault();
    private static final double FUEL_PRICE = 1.5;

    public synchronized Company getCompany() {
        return company;
    }

    public synchronized List<Job> getAvailableJobs() {
        if (availableJobs.isEmpty()) {
            generateJobs();
        }
        return List.copyOf(availableJobs);
    }

    private void generateJobs() {
        availableJobs.clear();
        for (int i = 0; i < 5; i++) {
            double distance = 100 + random.nextInt(900);
            double weight = 1 + random.nextInt(20);
            int days = 1 + random.nextInt(3);
            double reward = distance * 2 + weight * 10;
            availableJobs.add(new Job(distance, weight, days, reward));
        }
    }

    public synchronized boolean acceptJob(long jobId, long truckId) {
        Truck truck = company.getTrucks().stream()
                .filter(t -> t.getId() == truckId)
                .findFirst()
                .orElse(null);
        Job job = availableJobs.stream()
                .filter(j -> j.getId() == jobId)
                .findFirst()
                .orElse(null);
        if (truck == null || job == null) {
            return false;
        }
        double fuelCost = job.getDistance() * truck.getFuelConsumption() / 100.0 * FUEL_PRICE;
        double maintenanceCost = job.getDistance() * 0.05;
        job.assignTruck(truck.getId(), fuelCost, maintenanceCost);
        company.getActiveJobs().add(job);
        availableJobs.remove(job);
        return true;
    }

    public synchronized void buyTruck(String name, double fuelConsumption, double price) {
        if (name == null || name.isBlank() || fuelConsumption <= 0 || price <= 0 || price > company.getCash()) {
            throw new GameRuleException("Truck details are invalid or the company has insufficient cash");
        }
        company.subtractCash(price);
        company.getTrucks().add(new Truck(name, fuelConsumption));
    }

    public synchronized void hireDriver(String name, double dailySalary) {
        if (name == null || name.isBlank() || dailySalary <= 0 || dailySalary > company.getCash()) {
            throw new GameRuleException("Driver details are invalid or the company has insufficient cash");
        }
        company.subtractCash(dailySalary); // hiring fee
        company.getDrivers().add(new Driver(name, dailySalary));
    }

    public synchronized void advanceDay() {
        Iterator<Job> it = company.getActiveJobs().iterator();
        while (it.hasNext()) {
            Job job = it.next();
            job.progress();
            if (job.isComplete()) {
                company.addCash(job.getReward() - job.getFuelCost() - job.getMaintenanceCost());
                it.remove();
            }
        }
        // pay driver salaries
        company.getDrivers().forEach(d -> company.subtractCash(d.getDailySalary()));
        // degrade trucks a bit
        company.getTrucks().forEach(t -> t.degrade(0.01));
    }
}
