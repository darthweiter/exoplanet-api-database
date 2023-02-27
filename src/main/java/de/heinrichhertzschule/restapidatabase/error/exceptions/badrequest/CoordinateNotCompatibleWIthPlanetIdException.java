package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class CoordinateNotCompatibleWIthPlanetIdException extends BadRequestException{

  public CoordinateNotCompatibleWIthPlanetIdException() {
    super("The coordinates are not valid for this planet id.");
  }
}
