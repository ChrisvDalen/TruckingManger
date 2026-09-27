package io.github.chrisvdalen.truckingmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BuyTruckRequest(
        @NotBlank String name,
        @Positive double consumption,
        @Positive double price) {
}
