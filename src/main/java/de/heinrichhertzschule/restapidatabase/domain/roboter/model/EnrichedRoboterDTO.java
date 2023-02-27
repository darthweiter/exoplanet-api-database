package de.heinrichhertzschule.restapidatabase.domain.roboter.model;

public record EnrichedRoboterDTO(
    long rid,
    Long kid,
    Integer x,
    Integer y,
    long pid,
    Long rtid,
    String direction,
    String name,
    double energy,
    double robotTemperature,
    long sid,
    String status, boolean heater, boolean cooler) {

}
