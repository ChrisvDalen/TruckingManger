package com.example.truckingmanager.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.truckingmanager.service.GameService;

@RestController
@RequestMapping("/api/drivers")
public class DriversController {
    private final GameService service;

    public DriversController(GameService service) {
        this.service = service;
    }

    @PostMapping("/hire")
    public Object hire(@RequestBody Map<String, Object> body) {
        String name = (String) body.getOrDefault("name", "Driver");
        double salary = ((Number) body.getOrDefault("salary", 200)).doubleValue();
        service.hireDriver(name, salary);
        return service.getCompany();
    }
}
