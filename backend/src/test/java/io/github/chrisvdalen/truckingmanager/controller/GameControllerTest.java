package io.github.chrisvdalen.truckingmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.chrisvdalen.truckingmanager.model.Company;

class GameControllerTest extends ApiTestBase {

    @Test
    void statusReturnsInitialCompanySnapshot() {
        Company company = readCompany();

        assertThat(company.getCash()).isEqualTo(50_000);
        assertThat(company.getTrucks()).hasSize(1);
        assertThat(company.getTrucks().get(0).getName()).isEqualTo("Starter Truck");
        assertThat(company.getDrivers()).isEmpty();
        assertThat(company.getActiveJobs()).isEmpty();
    }

    @Test
    void advanceDegradesTrucksByOnePercent() {
        rest.post().uri("/api/game/advance")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.trucks[0].condition").isEqualTo(0.99);
    }
}
