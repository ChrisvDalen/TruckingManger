package io.github.chrisvdalen.truckingmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.chrisvdalen.truckingmanager.model.Company;

class TrucksControllerTest extends ApiTestBase {

    @Test
    void buyValidTruckReturnsCompany() {
        double beforeCash = readCompany().getCash();
        String name = "test-truck-" + System.nanoTime();

        rest.post().uri("/api/trucks/buy")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"" + name + "\",\"consumption\":40,\"price\":10000}")
                .exchange()
                .expectStatus().isOk();

        Company after = readCompany();
        assertThat(after.getTrucks()).anySatisfy(t -> {
            assertThat(t.getName()).isEqualTo(name);
            assertThat(t.getFuelConsumption()).isEqualTo(40);
        });
        assertThat(after.getCash()).isEqualTo(beforeCash - 10_000);
    }

    @Test
    void blankNameIsRejectedWith400() {
        rest.post().uri("/api/trucks/buy")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"   \",\"consumption\":40,\"price\":10000}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    void negativeConsumptionIsRejectedWith400() {
        rest.post().uri("/api/trucks/buy")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Big Rig\",\"consumption\":-5,\"price\":10000}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    void insufficientCashIsRejectedWith400() {
        rest.post().uri("/api/trucks/buy")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Big Rig\",\"consumption\":40,\"price\":99999999}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Truck details are invalid or the company has insufficient cash");
    }

    @Test
    void missingNameFailsValidation() {
        rest.post().uri("/api/trucks/buy")
                .header("Content-Type", "application/json")
                .body("{\"consumption\":40,\"price\":10000}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    void wrongTypeFieldIsRejectedWith400() {
        // A non-numeric price used to blow up the app with a 500 ClassCastException.
        rest.post().uri("/api/trucks/buy")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Big Rig\",\"consumption\":40,\"price\":\"not-a-number\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
