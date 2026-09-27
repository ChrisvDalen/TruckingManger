package io.github.chrisvdalen.truckingmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record HireDriverRequest(
        @NotBlank String name,
        @Positive double salary) {
}
