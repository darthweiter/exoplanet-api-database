package de.heinrichhertzschule.restapidatabase.domain.planet.model;

import java.util.List;

public record PlanetDetailsResponseDTO(PlanetResponseDTO planet, List<PlanetFieldResponseDTO> planetFields) {
}
