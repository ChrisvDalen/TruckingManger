package io.github.chrisvdalen.truckingmanager.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.chrisvdalen.truckingmanager.dto.AcceptJobRequest;
import io.github.chrisvdalen.truckingmanager.model.Job;
import io.github.chrisvdalen.truckingmanager.service.GameService;

@RestController
@RequestMapping("/api/jobs")
public class JobsController {
    private final GameService service;

    public JobsController(GameService service) {
        this.service = service;
    }

    @GetMapping("/available")
    public List<Job> available() {
        return service.getAvailableJobs();
    }

    @PostMapping("/accept")
    public Map<String, Boolean> accept(@Valid @RequestBody AcceptJobRequest request) {
        boolean ok = service.acceptJob(request.jobId(), request.truckId());
        return Map.of("accepted", ok);
    }
}
