package de.heinrichhertzschule.restapidatabase.domain.planet.model;

import jakarta.validation.constraints.NotNull;

public record PlanetRequestDTO(@NotNull String name, @NotNull Integer width, @NotNull Integer height) {
}
