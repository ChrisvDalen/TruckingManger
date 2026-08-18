package io.github.chrisvdalen.truckingmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.chrisvdalen.truckingmanager.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public Object status() {
        return service.getCompany();
    }

    @PostMapping("/advance")
    public Object advance() {
        service.advanceDay();
        return service.getCompany();
    }
}
