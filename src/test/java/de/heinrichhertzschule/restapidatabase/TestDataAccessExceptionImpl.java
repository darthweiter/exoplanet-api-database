package de.heinrichhertzschule.restapidatabase;

import org.springframework.dao.DataAccessException;

public class TestDataAccessExceptionImpl extends DataAccessException {

  public TestDataAccessExceptionImpl() {
    super("Es ist eine Test Exception");
  }
}
