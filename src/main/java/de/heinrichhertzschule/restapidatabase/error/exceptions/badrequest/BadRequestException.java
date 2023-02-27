package de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest;

import de.heinrichhertzschule.restapidatabase.error.exceptions.RestException;

public abstract class BadRequestException extends RestException {

  public BadRequestException(String moreInformation) {
    super("Bad Request", 400, moreInformation);
  }
}
