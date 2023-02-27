package de.heinrichhertzschule.restapidatabase.domain.roboter.model;

import jakarta.validation.constraints.NotNull;

public record RoboterInsertRequestDTO(@NotNull Long pid, @NotNull String name) {

}
