package de.heinrichhertzschule.restapidatabase.error.exceptions;

public abstract class RestException extends Exception {
  private final int errorCode;
  private final String moreInformation;

  public RestException(String message, int errorCode, String moreInformation) {
    super(message);
    this.errorCode = errorCode;
    this.moreInformation = moreInformation;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public String getMoreInformation() {
    return moreInformation;
  }
}
