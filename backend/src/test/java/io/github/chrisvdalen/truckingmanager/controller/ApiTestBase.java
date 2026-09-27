package io.github.chrisvdalen.truckingmanager.controller;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.client.RestTestClient;

import io.github.chrisvdalen.truckingmanager.model.Company;
import io.github.chrisvdalen.truckingmanager.service.GameService;
import io.github.chrisvdalen.truckingmanager.web.GlobalExceptionHandler;

/**
 * Controller tests use a standalone {@link RestTestClient} bound to the real
 * controllers backed by a fresh {@link GameService} per test.
 *
 * <p>Spring Boot 4 dropped the {@code @WebMvcTest} slice and its auto-configured
 * {@code MockMvc}. Binding a {@code RestTestClient} directly to the controllers
 * (standalone MockMvc) gives us a real HTTP binding — routing, JSON
 * (de)serialization, {@code @Valid} handling and the {@link GlobalExceptionHandler}
 * advice — without booting the whole application context, and keeps every test
 * isolated on its own game instance.
 */
abstract class ApiTestBase {
    GameService service;
    RestTestClient rest;

    @BeforeEach
    void prepare() {
        service = new GameService();
        rest = RestTestClient
                .bindToController(
                        new GameController(service),
                        new TrucksController(service),
                        new DriversController(service),
                        new JobsController(service))
                .configureServer(builder -> {
                    builder.setControllerAdvice(new GlobalExceptionHandler());
                    builder.setMessageConverters(new JacksonJsonHttpMessageConverter());
                })
                .build();
    }

    /**
     * Returns the live {@link Company} owned by the shared {@code GameService}.
     * Reading from the real bean (rather than deserializing the HTTP response)
     * is deliberate: the model classes assign their id in the constructor from
     * a static counter and are not round-trippable through Jackson, so a
     * deserialized copy would carry a bogus id.
     */
    Company readCompany() {
        return service.getCompany();
    }
}
