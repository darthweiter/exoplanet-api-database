package de.heinrichhertzschule.restapidatabase.domain.planet.model;

public record EnrichedPlanetDTO(
    long pid,
    Long kid,
    Integer x,
    Integer y,
    String planetName,
    int width,
    int height,
    String ground,
    Double Temperature,
    Long rid,
    String direction,
    String roboterName,
    Double energy,
    Double robotTemperature,
    String status,
    Boolean heater,
    Boolean cooler
    ) {

}
