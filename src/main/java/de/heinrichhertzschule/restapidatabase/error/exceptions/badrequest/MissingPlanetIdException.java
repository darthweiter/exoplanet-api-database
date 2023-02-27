package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class MissingPlanetIdException extends BadRequestException{

  public MissingPlanetIdException() {
    super("The Planet Id is missing.");
  }
}
