package io.github.chrisvdalen.truckingmanager.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameServiceTest {
    @Test
    void startsWithOneTruckAndGeneratedJobs() {
        var service = new GameService();

        assertEquals(1, service.getCompany().getTrucks().size());
        assertEquals(5, service.getAvailableJobs().size());
    }

    @Test
    void rejectsInvalidPurchases() {
        var service = new GameService();

        assertThrows(GameRuleException.class, () -> service.buyTruck("", 30, 10_000));
        assertThrows(GameRuleException.class, () -> service.buyTruck("Truck", 30, 100_000));
    }

    @Test
    void rejectsUnknownJobAndTruck() {
        var service = new GameService();

        assertFalse(service.acceptJob(-1, -1));
    }
}
