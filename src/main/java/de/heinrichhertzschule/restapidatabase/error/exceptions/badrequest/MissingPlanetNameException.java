package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class MissingPlanetNameException extends BadRequestException{

  public MissingPlanetNameException() {
    super("The Planet-name can't be empty");
  }
}
