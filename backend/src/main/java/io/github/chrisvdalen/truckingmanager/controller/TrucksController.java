package io.github.chrisvdalen.truckingmanager.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.chrisvdalen.truckingmanager.service.GameService;

@RestController
@RequestMapping("/api/trucks")
public class TrucksController {
    private final GameService service;

    public TrucksController(GameService service) {
        this.service = service;
    }

    @PostMapping("/buy")
    public Object buy(@RequestBody Map<String, Object> body) {
        String name = (String) body.getOrDefault("name", "Truck");
        double consumption = ((Number) body.getOrDefault("consumption", 30)).doubleValue();
        double price = ((Number) body.getOrDefault("price", 10000)).doubleValue();
        service.buyTruck(name, consumption, price);
        return service.getCompany();
    }
}
