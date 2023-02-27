package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

public class DirectionNotFoundException extends NotFoundException{

  public DirectionNotFoundException(String moreInformation) {
    super("The direction: " + moreInformation + " was not found");
  }
}
