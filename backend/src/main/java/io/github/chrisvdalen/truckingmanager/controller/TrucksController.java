package io.github.chrisvdalen.truckingmanager.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.chrisvdalen.truckingmanager.dto.BuyTruckRequest;
import io.github.chrisvdalen.truckingmanager.model.Company;
import io.github.chrisvdalen.truckingmanager.service.GameService;

@RestController
@RequestMapping("/api/trucks")
public class TrucksController {
    private final GameService service;

    public TrucksController(GameService service) {
        this.service = service;
    }

    @PostMapping("/buy")
    public Company buy(@Valid @RequestBody BuyTruckRequest request) {
        service.buyTruck(request.name(), request.consumption(), request.price());
        return service.getCompany();
    }
}
