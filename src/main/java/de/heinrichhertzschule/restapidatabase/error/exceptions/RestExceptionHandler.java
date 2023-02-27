package de.heinrichhertzschule.restapidatabase.error.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterInsertRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterRequestDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.BadRequestException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.model.ErrorResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private final static int BAD_REQUEST_ERROR_CODE = 400;
  private final static String REQUEST_BODY_WRONG_FORMAT = "The request body has wrong Format.";

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadRequestException(BadRequestException ex) {
    return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), ex.getErrorCode(), ex.getMoreInformation()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDTO> handleNoSuchFieldException(MethodArgumentTypeMismatchException ex) {
    return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), BAD_REQUEST_ERROR_CODE, ex.getLocalizedMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return new ResponseEntity<>(new ErrorResponseDTO(REQUEST_BODY_WRONG_FORMAT, BAD_REQUEST_ERROR_CODE, showCorrectMessage(ex.getMessage())),
        HttpStatus.BAD_REQUEST);
  }


  @Override
  public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), BAD_REQUEST_ERROR_CODE, ex.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
  }

  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), BAD_REQUEST_ERROR_CODE, ex.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException ex) {
    return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), ex.getErrorCode(), ex.getMoreInformation()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InternalErrorException.class)
  public ResponseEntity<ErrorResponseDTO> handleInternalErrorException(InternalErrorException ex) {
    return new ResponseEntity<>(new ErrorResponseDTO(ex.getMessage(), ex.getErrorCode(), ex.getMoreInformation()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponseDTO> handleNullPointerException(NullPointerException ex) {
    
    if(ex.getMessage().contains("RequestDTO")) {
      return new ResponseEntity<>(new ErrorResponseDTO(REQUEST_BODY_WRONG_FORMAT, BAD_REQUEST_ERROR_CODE, showCorrectMessage(ex.getMessage())),
          HttpStatus.BAD_REQUEST);
    } else {
      final InternalErrorException internalErrorException = new InternalErrorException();
      return new ResponseEntity<>(new ErrorResponseDTO(internalErrorException.getMessage(), internalErrorException.getErrorCode(), internalErrorException.getLocalizedMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String showCorrectMessage(String input) {
    ObjectMapper mapper = new ObjectMapper();
    try {
    if(input.contains("PlanetRequestDTO")) {
        return "The request Body must be in JSON-format of PlanetRequestDTO: " + mapper.writeValueAsString(new PlanetRequestDTO("planet-name", 0, 0));
    } else if(input.contains("MeasureRequestDTO")) {
        return "The request Body must be in JSON-format of MeasureRequestDTO:" + mapper.writeValueAsString(new MeasureRequestDTO(1L, 0, 0, "ground", 0.01));
    } else if(input.contains("RoboterRequestDTO")) {
        return "The request Body must be in JSON-format of RoboterRequestDTO:" + mapper.writeValueAsString(new RoboterRequestDTO(0, 0, 1L, "direction", "name", 99.99, 0.01, "status", false, false));
    } else if(input.contains("RoboterInsertRequestDTO")) {
      return "The request Body must be in JSON-format of RoboterInsertRequestDTO:" + mapper.writeValueAsString(new RoboterInsertRequestDTO(1L, "name"));
    }
    } catch (JsonProcessingException e) {
      //empty
    }
    return "null";
  }
}
