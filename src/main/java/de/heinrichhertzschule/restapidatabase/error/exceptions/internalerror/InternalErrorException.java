package de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror;

import de.heinrichhertzschule.restapidatabase.error.exceptions.RestException;

public class InternalErrorException extends RestException {

  public InternalErrorException() {
    super("there was a internal error", 500, "No Information");
  }
}
