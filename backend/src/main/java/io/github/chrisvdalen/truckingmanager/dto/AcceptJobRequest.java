package io.github.chrisvdalen.truckingmanager.dto;

import jakarta.validation.constraints.NotNull;

public record AcceptJobRequest(
        @NotNull Long jobId,
        @NotNull Long truckId) {
}
