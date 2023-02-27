package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

public class UpdateFailedException extends BadRequestException{

  public UpdateFailedException(String message) {
    super("The update failed: " + message);
  }
}
