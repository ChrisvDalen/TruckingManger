package io.github.chrisvdalen.truckingmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import io.github.chrisvdalen.truckingmanager.model.Company;

class JobsControllerTest extends ApiTestBase {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void availableReturnsFiveJobs() throws Exception {
        String body = new String(
                rest.get().uri("/api/jobs/available")
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult()
                        .getResponseBodyContent(),
                StandardCharsets.UTF_8);

        JsonNode jobs = objectMapper.readTree(body);
        assertThat(jobs.isArray()).isTrue();
        assertThat(jobs.size()).isEqualTo(5);
        assertThat(jobs.get(0).get("id").asLong()).isPositive();
        assertThat(jobs.get(0).get("distance").asDouble()).isGreaterThanOrEqualTo(100);
        assertThat(jobs.get(0).get("reward").asDouble()).isPositive();
    }

    @Test
    void acceptWithUnknownIdsIsGracefullyRejected() {
        rest.post().uri("/api/jobs/accept")
                .header("Content-Type", "application/json")
                .body("{\"jobId\":-1,\"truckId\":-1}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accepted").isEqualTo(false);
    }

    @Test
    void acceptWithoutRequiredFieldsIsRejected() {
        rest.post().uri("/api/jobs/accept")
                .header("Content-Type", "application/json")
                .body("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    void acceptWithWrongTypesIsRejected() {
        rest.post().uri("/api/jobs/accept")
                .header("Content-Type", "application/json")
                .body("{\"jobId\":\"abc\",\"truckId\":1}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void acceptMovesAvailableJobToActive() throws Exception {
        Company company = readCompany();
        long truckId = company.getTrucks().get(0).getId();

        String jobsBody = new String(
                rest.get().uri("/api/jobs/available")
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult()
                        .getResponseBodyContent(),
                StandardCharsets.UTF_8);
        long jobId = objectMapper.readTree(jobsBody).get(0).get("id").asLong();

        rest.post().uri("/api/jobs/accept")
                .header("Content-Type", "application/json")
                .body("{\"jobId\":" + jobId + ",\"truckId\":" + truckId + "}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accepted").isEqualTo(true);

        Company after = readCompany();
        assertThat(after.getActiveJobs()).anySatisfy(job -> {
            assertThat(job.getId()).isEqualTo(jobId);
            assertThat(job.getAssignedTruckId()).isEqualTo(truckId);
        });
    }
}
