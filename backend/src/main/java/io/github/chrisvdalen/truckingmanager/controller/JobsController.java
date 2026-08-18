package io.github.chrisvdalen.truckingmanager.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.chrisvdalen.truckingmanager.service.GameService;

@RestController
@RequestMapping("/api/jobs")
public class JobsController {
    private final GameService service;

    public JobsController(GameService service) {
        this.service = service;
    }

    @GetMapping("/available")
    public Object available() {
        return service.getAvailableJobs();
    }

    @PostMapping("/accept")
    public Object accept(@RequestBody Map<String, Long> body) {
        long jobId = body.getOrDefault("jobId", -1L);
        long truckId = body.getOrDefault("truckId", -1L);
        boolean ok = service.acceptJob(jobId, truckId);
        return Map.of("accepted", ok);
    }
}
