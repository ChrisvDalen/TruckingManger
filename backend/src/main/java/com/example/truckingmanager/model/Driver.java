package com.example.truckingmanager.model;

public class Driver {
    private static long nextId = 1;
    private long id;
    private String name;
    private double dailySalary;

    public Driver(String name, double dailySalary) {
        this.id = nextId++;
        this.name = name;
        this.dailySalary = dailySalary;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public double getDailySalary() { return dailySalary; }
}
