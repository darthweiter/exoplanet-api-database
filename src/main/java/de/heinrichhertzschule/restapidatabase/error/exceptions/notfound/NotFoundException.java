package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

import de.heinrichhertzschule.restapidatabase.error.exceptions.RestException;

public abstract class NotFoundException extends RestException {

  public NotFoundException(String moreInformation) {
    super("Not Found", 404, moreInformation);
  }
}
