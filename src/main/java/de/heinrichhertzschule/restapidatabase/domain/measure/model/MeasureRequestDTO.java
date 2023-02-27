package de.heinrichhertzschule.restapidatabase.domain.measure.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MeasureRequestDTO(@NotNull Long pid, @NotNull Integer x, @NotNull Integer y, @NotNull String ground, @Schema(example = "0.01") @NotNull Double temperature) {

}
