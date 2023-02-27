package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

public class PlanetNotFoundException extends NotFoundException{

  public PlanetNotFoundException(String moreInformation) {
    super("The planet: " + moreInformation + " was not found.");
  }
}
