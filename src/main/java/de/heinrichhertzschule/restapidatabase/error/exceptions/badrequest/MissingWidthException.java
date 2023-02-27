package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class MissingWidthException extends BadRequestException{

  public MissingWidthException() {
    super("The Width of the planet must be greater 0");
  }
}
