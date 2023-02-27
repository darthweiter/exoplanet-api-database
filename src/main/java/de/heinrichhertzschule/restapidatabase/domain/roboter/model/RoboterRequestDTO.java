package de.heinrichhertzschule.restapidatabase.domain.roboter.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RoboterRequestDTO(@NotNull Integer x, @NotNull Integer y, @NotNull Long pid,
                                @NotNull String direction, @NotNull String name,
                                @Schema(example = "100.00") @NotNull Double energy,
                                @Schema(example = "0.01") @NotNull Double temperature,
                                @NotNull String status, Boolean heater, Boolean cooler) {

}
