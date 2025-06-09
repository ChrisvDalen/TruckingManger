package com.example.truckingmanager.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.truckingmanager.model.Company;
import com.example.truckingmanager.model.Driver;
import com.example.truckingmanager.model.Job;
import com.example.truckingmanager.model.Truck;

@Service
public class GameService {
    private Company company = new Company();
    private List<Job> availableJobs = new ArrayList<>();
    private Random random = new Random();
    private double fuelPrice = 1.5; // per liter

    public Company getCompany() {
        return company;
    }

    public List<Job> getAvailableJobs() {
        if (availableJobs.isEmpty()) {
            generateJobs();
        }
        return availableJobs;
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

    public boolean acceptJob(long jobId, long truckId) {
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
        double fuelCost = job.getDistance() * truck.getFuelConsumption() / 100.0 * fuelPrice;
        double maintenanceCost = job.getDistance() * 0.05;
        job.assignTruck(truck.getId(), fuelCost, maintenanceCost);
        company.getActiveJobs().add(job);
        availableJobs.remove(job);
        return true;
    }

    public void buyTruck(String name, double fuelConsumption, double price) {
        company.subtractCash(price);
        company.getTrucks().add(new Truck(name, fuelConsumption));
    }

    public void hireDriver(String name, double dailySalary) {
        company.subtractCash(dailySalary); // hiring fee
        company.getDrivers().add(new Driver(name, dailySalary));
    }

    public void advanceDay() {
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
