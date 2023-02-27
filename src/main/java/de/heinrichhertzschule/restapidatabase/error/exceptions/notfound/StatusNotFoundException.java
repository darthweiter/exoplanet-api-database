package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

public class StatusNotFoundException extends NotFoundException{

  public StatusNotFoundException(String moreInformation) {
    super("status " + moreInformation + " not found.");
  }
}
