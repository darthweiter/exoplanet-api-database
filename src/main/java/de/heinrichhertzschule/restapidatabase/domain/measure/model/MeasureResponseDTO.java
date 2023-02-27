package de.heinrichhertzschule.restapidatabase.domain.measure.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record MeasureResponseDTO(long id, int x, int y, long pid, String ground, @Schema(example = "0.01") double temperature) {

}
