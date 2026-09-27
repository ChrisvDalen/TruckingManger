package io.github.chrisvdalen.truckingmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.github.chrisvdalen.truckingmanager.model.Company;

class DriversControllerTest extends ApiTestBase {

    @Test
    void hireValidDriverReturnsCompany() {
        double beforeCash = readCompany().getCash();
        String name = "test-driver-" + System.nanoTime();

        rest.post().uri("/api/drivers/hire")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"" + name + "\",\"salary\":200}")
                .exchange()
                .expectStatus().isOk();

        Company after = readCompany();
        assertThat(after.getDrivers()).anySatisfy(d -> {
            assertThat(d.getName()).isEqualTo(name);
            assertThat(d.getDailySalary()).isEqualTo(200);
        });
        assertThat(after.getCash()).isEqualTo(beforeCash - 200);
    }

    @Test
    void zeroSalaryIsRejectedWith400() {
        rest.post().uri("/api/drivers/hire")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Ann\",\"salary\":0}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    void blankNameIsRejectedWith400() {
        rest.post().uri("/api/drivers/hire")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"\",\"salary\":200}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    void salaryAboveCashIsRejectedWith400() {
        rest.post().uri("/api/drivers/hire")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Ann\",\"salary\":99999999}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Driver details are invalid or the company has insufficient cash");
    }
}
