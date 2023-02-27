package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

public class CoordinateNotFoundException extends NotFoundException{


  public CoordinateNotFoundException(String moreInformation) {
    super("The coordinate with: " + moreInformation + "is not found");
  }
}
