package de.heinrichhertzschule.restapidatabase.domain.roboter;

import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterInsertRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.BadRequestException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.GeneralBadRequestException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.NotFoundException;
import de.heinrichhertzschule.restapidatabase.error.model.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roboter")
public class RoboterController {
  private final RoboterService roboterService;

  public RoboterController(RoboterService roboterService) {
    this.roboterService = roboterService;
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      array = @ArraySchema(schema = @Schema(implementation = RoboterResponseDTO.class)))
              }),
          @ApiResponse(
              responseCode = "500",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              })
      })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<RoboterResponseDTO>> getAll()
      throws InternalErrorException {
    return new ResponseEntity<>(roboterService.getAll(), HttpStatus.OK);
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = RoboterResponseDTO.class))
              }),
          @ApiResponse(
              responseCode = "400",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              }),
          @ApiResponse(
              responseCode = "404",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              }),
          @ApiResponse(
              responseCode = "500",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              })
      })
  @RequestMapping(method = RequestMethod.GET, path = "/{id}")
  public ResponseEntity<RoboterResponseDTO> getRoboterById(@PathVariable long id)
      throws NotFoundException, InternalErrorException {
    return new ResponseEntity<>(roboterService.getById(id), HttpStatus.OK);
  }
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = RoboterResponseDTO.class))
              }),
          @ApiResponse(
              responseCode = "400",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              }),
          @ApiResponse(
              responseCode = "500",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              })
      })
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<RoboterResponseDTO> saveRobot(@RequestBody @Valid RoboterInsertRequestDTO roboter)
      throws BadRequestException, InternalErrorException {
    return new ResponseEntity<>(roboterService.insert(roboter), HttpStatus.OK);
  }


  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = RoboterResponseDTO.class))
              }),
          @ApiResponse(
              responseCode = "400",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              }),
          @ApiResponse(
              responseCode = "500",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = ErrorResponseDTO.class))
              })
      })
  @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
  public ResponseEntity<RoboterResponseDTO> updateRobot(@PathVariable long id, @RequestBody @Valid RoboterRequestDTO roboter)
      throws BadRequestException, InternalErrorException {
    if(roboter.x() == null || roboter.y() == null || roboter.x() < 0 || roboter.y() < 0) {
      throw new GeneralBadRequestException("Coordinate must be >= 0");
    }
    return new ResponseEntity<>(roboterService.update(id, roboter), HttpStatus.OK);
  }
}
