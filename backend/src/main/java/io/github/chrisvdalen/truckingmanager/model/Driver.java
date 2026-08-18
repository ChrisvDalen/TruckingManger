package io.github.chrisvdalen.truckingmanager.model;

public class Driver {
    private static final java.util.concurrent.atomic.AtomicLong NEXT_ID = new java.util.concurrent.atomic.AtomicLong(1);
    private final long id;
    private final String name;
    private final double dailySalary;

    public Driver(String name, double dailySalary) {
        this.id = NEXT_ID.getAndIncrement();
        this.name = name;
        this.dailySalary = dailySalary;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public double getDailySalary() { return dailySalary; }
}
