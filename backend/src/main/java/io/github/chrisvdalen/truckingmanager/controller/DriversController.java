package io.github.chrisvdalen.truckingmanager.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.chrisvdalen.truckingmanager.dto.HireDriverRequest;
import io.github.chrisvdalen.truckingmanager.model.Company;
import io.github.chrisvdalen.truckingmanager.service.GameService;

@RestController
@RequestMapping("/api/drivers")
public class DriversController {
    private final GameService service;

    public DriversController(GameService service) {
        this.service = service;
    }

    @PostMapping("/hire")
    public Company hire(@Valid @RequestBody HireDriverRequest request) {
        service.hireDriver(request.name(), request.salary());
        return service.getCompany();
    }
}
