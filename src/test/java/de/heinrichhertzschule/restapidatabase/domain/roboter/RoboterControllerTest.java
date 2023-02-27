package de.heinrichhertzschule.restapidatabase.domain.roboter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.roboter.RoboterController;
import de.heinrichhertzschule.restapidatabase.domain.roboter.RoboterService;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterInsertRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.RestExceptionHandler;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.RoboterNotFoundException;
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
public class RoboterControllerTest {

  private MockMvc mvc;

  @Mock
  private RoboterService roboterService;

  @InjectMocks
  private RoboterController roboterController;

  private final static String API_V1_ROBOTER = "/api/v1/roboter";

  @BeforeEach
  public void setup() {
    mvc = MockMvcBuilders.standaloneSetup(roboterController)
        .setControllerAdvice(new RestExceptionHandler())
        .build();
  }

  @Test
  public void getAll_successfullyRequest_returnsRoboter() throws Exception {
    List<RoboterResponseDTO> testData = List.of(
        new RoboterResponseDTO(19L, 80L, 0, 1, "RoboterName", 0.1, 0.22, "Direction", "status", false, true));
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(roboterService.getAll()).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(get(API_V1_ROBOTER)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void getRoboterById_successfullyRequest_returnsRoboter() throws Exception {
    long id = 832L;
    RoboterResponseDTO testData = new RoboterResponseDTO(id, 80L, 0, 1, "RoboterName", 0.1, 0.22,
        "Direction", "status", true, true);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(roboterService.getById(id)).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(get(API_V1_ROBOTER + "/" + id)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void getRoboterById_notFoundException_returnsErrorCodeNotFound() throws Exception {
    long id = 832L;
    given(roboterService.getById(id)).willThrow(
        new RoboterNotFoundException("Wurde nicht gefunden"));

    MockHttpServletResponse response = mvc.perform(get(API_V1_ROBOTER + "/" + id)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void getRoboterById_badRequestException_returnsErrorCodeBadRequest() throws Exception {
    MockHttpServletResponse response = mvc.perform(get(API_V1_ROBOTER + "/" + "p")
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void saveRobot_successfullyRequest_returnsRobot() throws Exception {
    RoboterInsertRequestDTO testRequest = new RoboterInsertRequestDTO(15L, "RoboterName");
    RoboterResponseDTO testResponse = new RoboterResponseDTO(99L, 15L, null, null, "RoboterName",
        0.00, 100.00, null, "OK", false, false);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testResponse);
    given(roboterService.insert(testRequest)).willReturn(testResponse);

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_ROBOTER).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void saveRobot_badBody_returnsErrorCodeBadRequest() throws Exception {
    Map<String, String> testRequest = new HashMap<>();
    testRequest.put("name", "test");
    ObjectMapper mapper = new ObjectMapper();

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_ROBOTER).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void saveRobot_InternalServerException_returnsErrorCodeInternError() throws Exception {
    RoboterInsertRequestDTO testRequest = new RoboterInsertRequestDTO(15L, "RoboterName");
    ObjectMapper mapper = new ObjectMapper();
    given(roboterService.insert(testRequest)).willThrow(new InternalErrorException());

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_ROBOTER).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

  @Test
  public void updateRobot_successfullyRequest_returnsRobot() throws Exception {
    long id = 10L;
    RoboterRequestDTO testRequest = new RoboterRequestDTO(0, 0, 9L, "NORTH", "R1", 100.00, 2.00, "WORKING", false, false);
    RoboterResponseDTO testResponse = new RoboterResponseDTO(id, 15L, 0, 0, "RoboterName",
        2.00, 100.00, "NORTH", "WORKING", true, false);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testResponse);
    given(roboterService.update(id, testRequest)).willReturn(testResponse);

    MockHttpServletResponse response = mvc.perform(
            put(API_V1_ROBOTER + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void updateRoboter_badBody_returnsErrorCodeBadRequest() throws Exception {
    long id =13924L;
    Map<String, String> testRequest = new HashMap<>();
    testRequest.put("name", "test");
    ObjectMapper mapper = new ObjectMapper();

    MockHttpServletResponse response = mvc.perform(
            put(API_V1_ROBOTER + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void updateRoboter_InternalServerException_returnsErrorCodeInternError() throws Exception {
    long id = 22300324935L;
    RoboterRequestDTO testRequest = new RoboterRequestDTO(0, 0, 9L, "NORTH", "R1", 100.00, 2.00, "WORKING", false, false);
    ObjectMapper mapper = new ObjectMapper();
    given(roboterService.update(id, testRequest)).willThrow(new InternalErrorException());

    MockHttpServletResponse response = mvc.perform(
            put(API_V1_ROBOTER + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

}
