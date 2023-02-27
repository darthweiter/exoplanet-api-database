package de.heinrichhertzschule.restapidatabase.domain.roboter.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record RoboterResponseDTO(
    long id,
    long pid,
    Integer x,
    Integer y,
    String name,
    @Schema(example = "0.01") double temperature,
    @Schema(example = "0.01") double energy,
    String direction,
    String status,
    boolean heater,
    boolean cooler
) {}
