package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

public class GroundNotFoundException extends NotFoundException{

  public GroundNotFoundException(String moreInformation) {
    super("No Ground: " + moreInformation + " was found");
  }
}
