package io.github.chrisvdalen.truckingmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.chrisvdalen.truckingmanager.model.Company;
import io.github.chrisvdalen.truckingmanager.model.Job;

class GameServiceTest {

    @Test
    void startsWithOneTruckAndGeneratedJobs() {
        var service = new GameService();

        assertEquals(1, service.getCompany().getTrucks().size());
        assertEquals(5, service.getAvailableJobs().size());
        assertEquals(50_000.0, service.getCompany().getCash(), 0.0001);
    }

    @Test
    void rejectsInvalidPurchases() {
        var service = new GameService();

        assertThrows(GameRuleException.class, () -> service.buyTruck("", 30, 10_000));
        assertThrows(GameRuleException.class, () -> service.buyTruck(null, 30, 10_000));
        assertThrows(GameRuleException.class, () -> service.buyTruck("  ", 30, 10_000));
        assertThrows(GameRuleException.class, () -> service.buyTruck("Truck", -1, 10_000));
        assertThrows(GameRuleException.class, () -> service.buyTruck("Truck", 30, 0));
        assertThrows(GameRuleException.class, () -> service.buyTruck("Truck", 30, 100_000));
        assertEquals(1, service.getCompany().getTrucks().size());
        assertEquals(50_000.0, service.getCompany().getCash(), 0.0001);
    }

    @Test
    void validTruckPurchaseDeductsCash() {
        var service = new GameService();

        service.buyTruck("Big Rig", 40, 10_000);

        assertEquals(2, service.getCompany().getTrucks().size());
        assertEquals(40_000.0, service.getCompany().getCash(), 0.0001);
        assertEquals("Big Rig", service.getCompany().getTrucks().get(1).getName());
    }

    @Test
    void rejectsInvalidDrivers() {
        var service = new GameService();

        assertThrows(GameRuleException.class, () -> service.hireDriver("", 200));
        assertThrows(GameRuleException.class, () -> service.hireDriver(null, 200));
        assertThrows(GameRuleException.class, () -> service.hireDriver("Ann", 0));
        assertThrows(GameRuleException.class, () -> service.hireDriver("Ann", -50));
        assertThrows(GameRuleException.class, () -> service.hireDriver("Ann", 50_000.01));
        assertEquals(0, service.getCompany().getDrivers().size());
    }

    @Test
    void hiringDriverDeductsHiringFee() {
        var service = new GameService();

        service.hireDriver("Ann", 200);

        assertEquals(1, service.getCompany().getDrivers().size());
        assertEquals(49_800.0, service.getCompany().getCash(), 0.0001);
    }

    @Test
    void rejectsUnknownJobAndTruck() {
        var service = new GameService();

        assertFalse(service.acceptJob(-1, -1));
        long anyJobId = service.getAvailableJobs().get(0).getId();
        long anyTruckId = service.getCompany().getTrucks().get(0).getId();
        assertFalse(service.acceptJob(anyJobId, -1));
        assertFalse(service.acceptJob(-1, anyTruckId));
        assertTrue(service.getCompany().getActiveJobs().isEmpty());
        assertEquals(5, service.getAvailableJobs().size());
    }

    @Test
    void acceptsValidJobAndMovesItToActive() {
        var service = new GameService();
        var company = service.getCompany();
        var truck = company.getTrucks().get(0);
        var job = service.getAvailableJobs().get(0);

        boolean accepted = service.acceptJob(job.getId(), truck.getId());

        assertTrue(accepted);
        assertTrue(job.isAssigned());
        assertEquals(truck.getId(), job.getAssignedTruckId());
        assertEquals(1, company.getActiveJobs().size());
        assertEquals(company.getActiveJobs().get(0), job);
        assertFalse(service.getAvailableJobs().contains(job));
    }

    @Test
    void acceptedJobPaysOutNetRewardAfterCompletion() {
        var service = new GameService();
        var company = service.getCompany();
        var truck = company.getTrucks().get(0);
        var job = service.getAvailableJobs().get(0);
        int remaining = job.getRemainingDays();

        assertTrue(service.acceptJob(job.getId(), truck.getId()));
        double cashBefore = company.getCash();
        double expectedNet = job.getReward() - job.getFuelCost() - job.getMaintenanceCost();

        for (int i = 0; i < remaining; i++) {
            service.advanceDay();
        }

        assertTrue(company.getActiveJobs().isEmpty());
        assertEquals(cashBefore + expectedNet, company.getCash(), 0.0001);
    }

    @Test
    void fuelCostScalesWithDistanceAndTruckConsumption() {
        var service = new GameService();
        var truck = service.getCompany().getTrucks().get(0);
        var job = service.getAvailableJobs().get(0);

        service.acceptJob(job.getId(), truck.getId());

        double expectedFuel = job.getDistance() * truck.getFuelConsumption() / 100.0 * 1.5;
        double expectedMaintenance = job.getDistance() * 0.05;
        assertEquals(expectedFuel, job.getFuelCost(), 0.0001);
        assertEquals(expectedMaintenance, job.getMaintenanceCost(), 0.0001);
    }

    @Test
    void advancingDaysPaysDriverSalaries() {
        var service = new GameService();
        var company = service.getCompany();

        service.hireDriver("Ann", 200);
        double cashBefore = company.getCash();
        service.advanceDay();

        assertEquals(cashBefore - 200, company.getCash(), 0.0001);
    }

    @Test
    void advancingDaysDegradesTrucksToOnePercentPerDay() {
        var service = new GameService();
        var truck = service.getCompany().getTrucks().get(0);

        for (int i = 0; i < 5; i++) {
            service.advanceDay();
        }

        assertEquals(0.95, truck.getCondition(), 0.0001);
    }

    @Test
    void truckConditionNeverGoesBelowZero() {
        var service = new GameService();
        var truck = service.getCompany().getTrucks().get(0);

        for (int i = 0; i < 200; i++) {
            service.advanceDay();
        }

        assertEquals(0.0, truck.getCondition(), 0.0001);
    }

    @Test
    void jobsRegenerateWhenAllAreAccepted() {
        var service = new GameService();
        var company = service.getCompany();
        var truckIds = company.getTrucks();

        // accept every available job on the starter truck
        long truckId = truckIds.get(0).getId();
        for (Job job : service.getAvailableJobs()) {
            assertTrue(service.acceptJob(job.getId(), truckId));
        }
        assertEquals(5, company.getActiveJobs().size());

        // asking again yields a fresh batch of different jobs
        var regenerated = service.getAvailableJobs();
        assertEquals(5, regenerated.size());
        for (Job job : regenerated) {
            assertTrue(job.getDurationDays() >= 1 && job.getDurationDays() <= 3);
            assertTrue(job.getReward() > 0);
            assertNotEquals(0, job.getDistance());
            assertNotNull(job);
        }
    }
}
