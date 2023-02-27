package de.heinrichhertzschule.restapidatabase.domain.planet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetController;
import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetService;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDetailsResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.RestExceptionHandler;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.PlanetNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class PlanetControllerTest {

  private MockMvc mvc;

  @Mock
  private PlanetService planetService;

  @InjectMocks
  private PlanetController planetController;

  @BeforeEach
  public void setup() {
    mvc = MockMvcBuilders.standaloneSetup(planetController)
        .setControllerAdvice(new RestExceptionHandler())
        .build();
  }

  private final static String API_V1_PLANETEN = "/api/v1/planeten";

  @Test
  public void getPlanets_successfullyRequest_returnsListOfPlanets() throws Exception {
    List<PlanetResponseDTO> testData = List.of(new PlanetResponseDTO(1L, "test", 1, 2));
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(planetService.getAllPlanets()).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void getPlanetDetails_successfullyRequest_returnsPlanetDetails() throws Exception {
    long testId = 2L;
    PlanetDetailsResponseDTO testData = new PlanetDetailsResponseDTO(
        new PlanetResponseDTO(testId, "Test", 10, 11),
        Collections.emptyList());
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(planetService.queryPlanetDetails(testId)).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + testId + "/details")
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void getPlanetDetails_badRequestException_returnsErrorCodeBadRequest() throws Exception {
    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + "x" + "/details")
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void getPlanetDetails_NotFoundException_returnsErrorCodeNotFound() throws Exception {
    long testId = 3L;

    given(planetService.queryPlanetDetails(testId)).willThrow(new PlanetNotFoundException("test"));

    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + testId + "/details")
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void getPlanetDetails_NotFoundException_returnsErrorCodeInternalError() throws Exception {
    long testId = 4L;

    given(planetService.queryPlanetDetails(testId)).willThrow(new InternalErrorException());

    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + testId + "/details")
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

  @Test
  public void getPlanetById_successfullyRequest_returnsPlanet() throws Exception {
    long testId = 99L;
    PlanetResponseDTO testData = new PlanetResponseDTO(testId, "Test", 15, 2);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(planetService.getPlanetById(testId)).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + testId)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void getPlanetById_badRequestException_returnsErrorCodeBadRequest() throws Exception {
    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + "x")
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void getPlanetById_NotFoundException_returnsErrorCodeNotFound() throws Exception {
    long testId = 29L;

    given(planetService.getPlanetById(testId)).willThrow(new PlanetNotFoundException("test"));

    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + testId)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void getPlanetById_InternalErrorException_returnsErrorCodeInternalError()
      throws Exception {
    long testId = 29L;

    given(planetService.getPlanetById(testId)).willThrow(new InternalErrorException());

    MockHttpServletResponse response = mvc.perform(get(API_V1_PLANETEN + "/" + testId)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

  @Test
  public void savePlanet_successfullyRequest_returnsPlanet() throws Exception {
    PlanetRequestDTO testRequestData = new PlanetRequestDTO("Neuer Name", 8, 7);
    PlanetResponseDTO testResponseData = new PlanetResponseDTO(8L, "Neuer Name", 8, 7);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testResponseData);

    given(planetService.insert(testRequestData)).willReturn(testResponseData);

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_PLANETEN).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequestData))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void savePlanet_badMediaType_returnsErrorCodeBadRequest() throws Exception {
    PlanetRequestDTO testRequestData = new PlanetRequestDTO(null, 8, 7);
    ObjectMapper mapper = new ObjectMapper();

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_PLANETEN).content(mapper.writeValueAsString(testRequestData))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void savePlanet_badRequestBody_returnsErrorCodeBadRequest() throws Exception {

    Map<String, String> testData = new HashMap<>();
    testData.put("1", "3");
    ObjectMapper mapper = new ObjectMapper();

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_PLANETEN).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testData))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

}
