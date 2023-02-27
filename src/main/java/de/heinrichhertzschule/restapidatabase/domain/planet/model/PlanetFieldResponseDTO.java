package de.heinrichhertzschule.restapidatabase.domain.planet.model;

import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PlanetFieldResponseDTO(int x, int y, String ground, @Schema(example = "0.01") Double temperature, @Schema( type = "RoboterResponseDTO") List<RoboterResponseDTO> roboter) {

}
