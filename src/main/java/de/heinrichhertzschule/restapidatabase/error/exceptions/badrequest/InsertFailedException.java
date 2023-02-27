package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class InsertFailedException extends BadRequestException{

  public InsertFailedException(String message) {
    super("The insert failed: " + message);
  }
}
