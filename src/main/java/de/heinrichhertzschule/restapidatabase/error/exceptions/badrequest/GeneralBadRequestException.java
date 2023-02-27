package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class GeneralBadRequestException extends BadRequestException{

  public GeneralBadRequestException(String moreInformation) {
    super("There was the following error: " + moreInformation);
  }
}
