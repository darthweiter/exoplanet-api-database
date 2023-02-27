package de.heinrichhertzschule.restapidatabase.error.exceptions.notfound;

public class MeasureNotFoundException extends NotFoundException{

  public MeasureNotFoundException(String moreInformation) {
    super("THe Measure: " + moreInformation + " was not found.");
  }
}
