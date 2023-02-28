package de.heinrichhertzschule.restapidatabase.domain.planet;

import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDetailsResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetRequestDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.BadRequestException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.MissingHeightException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.MissingPlanetNameException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.MissingWidthException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planeten")
public class PlanetController {
  private final static Logger logger = LoggerFactory.getLogger(PlanetController.class);
  private final PlanetService planetService;

  public PlanetController(PlanetService planetService) {
    this.planetService = planetService;
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      array = @ArraySchema(schema = @Schema(implementation = PlanetResponseDTO.class)))
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
  public ResponseEntity<List<PlanetResponseDTO>> getPlanets() {
    return new ResponseEntity<>(planetService.getAllPlanets(), HttpStatus.OK);
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = PlanetDetailsResponseDTO.class))
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
  @RequestMapping(method = RequestMethod.GET, value ="/{id}/details")
  public ResponseEntity<PlanetDetailsResponseDTO> getPlanetDetails(@PathVariable long id)
      throws InternalErrorException, NotFoundException, BadRequestException {
    return new ResponseEntity<>(planetService.queryPlanetDetails(id), HttpStatus.OK);
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = PlanetResponseDTO.class))
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
  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  public ResponseEntity<PlanetResponseDTO> getPlanetById(@PathVariable long id)
      throws BadRequestException, NotFoundException, InternalErrorException {
      return new ResponseEntity<>(planetService.getPlanetById(id), HttpStatus.OK);
  }

  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = PlanetResponseDTO.class))
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
  public ResponseEntity<PlanetResponseDTO> savePlanet(@RequestBody @Valid PlanetRequestDTO planet)
      throws BadRequestException, InternalErrorException {

    if(planet.name() == null || planet.name().equals("")) {
      logger.warn("POST-Request Exception: /api/v1/planeten body:" + planet.toString());
      throw new MissingPlanetNameException();
    }
    if(planet.width() <= 0) {
      logger.warn("POST-Request Exception: /api/v1/planeten body:" + planet.toString());
      throw new MissingWidthException();
    }
    if(planet.height() <= 0) {
      logger.warn("POST-Request Exception: /api/v1/planeten body:" + planet.toString());
      throw new MissingHeightException();
    }
    return new ResponseEntity<>(planetService.insert(planet), HttpStatus.OK);
  }
}


