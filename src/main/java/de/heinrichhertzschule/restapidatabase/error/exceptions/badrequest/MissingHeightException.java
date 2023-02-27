package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class MissingHeightException extends BadRequestException{

  public MissingHeightException() {
    super("The Height of the planet must be greater than 0");
  }
}
