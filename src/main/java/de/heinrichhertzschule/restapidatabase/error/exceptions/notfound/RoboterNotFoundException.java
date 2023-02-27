package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

public class RoboterNotFoundException extends NotFoundException{

  public RoboterNotFoundException(String moreInformation) {
    super("The roboter: "+ moreInformation + " was not found");
  }
}
