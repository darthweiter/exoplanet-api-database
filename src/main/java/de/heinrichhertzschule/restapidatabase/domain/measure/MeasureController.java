package de.heinrichhertzschule.restapidatabase.domain.measure;

import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.BadRequestException;
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
@RequestMapping("/api/v1/messdaten")
public class MeasureController {

  private final MeasureService measureService;

  public MeasureController(MeasureService measureService) {
    this.measureService = measureService;
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      array = @ArraySchema(schema = @Schema(implementation = MeasureResponseDTO.class)))
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
  public ResponseEntity<List<MeasureResponseDTO>> getAllMeasures() throws InternalErrorException {
    return new ResponseEntity<>(measureService.getAll(), HttpStatus.OK);
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = MeasureResponseDTO.class))
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
  public ResponseEntity<MeasureResponseDTO> getMeasureById(@PathVariable long id)
      throws NotFoundException, BadRequestException, InternalErrorException {
    return new ResponseEntity<>(measureService.selectById(id), HttpStatus.OK);
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = MeasureResponseDTO.class))
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
  public ResponseEntity<MeasureResponseDTO> saveMeasure(
      @RequestBody @Valid MeasureRequestDTO measure)
      throws BadRequestException, InternalErrorException {
    return new ResponseEntity<>(measureService.save(measure), HttpStatus.OK);
  }
}
