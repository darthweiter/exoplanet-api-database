package de.heinrichhertzschule.restapidatabase.domain.measure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.measure.MeasureController;
import de.heinrichhertzschule.restapidatabase.domain.measure.MeasureService;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.RestExceptionHandler;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.MeasureNotFoundException;
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
public class MeasureControllerTest {

  private MockMvc mvc;

  @Mock
  private MeasureService measureService;

  @InjectMocks
  private MeasureController measureController;

  private final static String API_V1_MESSDATEN = "/api/v1/messdaten";

  @BeforeEach
  public void setup() {
    mvc = MockMvcBuilders.standaloneSetup(measureController)
        .setControllerAdvice(new RestExceptionHandler())
        .build();
  }

  @Test
  public void getAllMeasures_successfullyRequest_returnsListOfMeasures() throws Exception {
    List<MeasureResponseDTO> testData = List.of(new MeasureResponseDTO(1L, 2, 1, 2, "Boden", 0.0));
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(measureService.getAll()).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(get(API_V1_MESSDATEN)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void getMeasureById_successfullyRequest_returnsMeasure() throws Exception {
    long id = 9L;
    MeasureResponseDTO testData = new MeasureResponseDTO(id, 9, 9, 2, "Test", 99.99);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(measureService.selectById(id)).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(get(API_V1_MESSDATEN + "/" + id)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void getMeasureById_notFoundExceptionHappens_returnsErrorCodeNotFound() throws Exception {
    long id = 15L;
    given(measureService.selectById(id)).willThrow(new MeasureNotFoundException("nicht gefunden"));

    MockHttpServletResponse response = mvc.perform(get(API_V1_MESSDATEN + "/" + id)
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void getMeasureById_BadRequestExceptionHappens_returnsErrorCodeBadRequest()
      throws Exception {
    MockHttpServletResponse response = mvc.perform(get(API_V1_MESSDATEN + "/" + "x")
            .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void saveMeasure_successfullyRequest_returnsMeasure() throws Exception {
    MeasureRequestDTO testRequest = new MeasureRequestDTO(7L, 0, 2, "Himmel", 0.02);
    MeasureResponseDTO testData = new MeasureResponseDTO(8L, 0, 2, 7L, "Himmel", 0.02);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(testData);
    given(measureService.save(testRequest)).willReturn(testData);

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_MESSDATEN).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

  @Test
  public void saveMeasure_notValidBody_returnsErrorCodeBadRequest() throws Exception {
    Map<String, String> testBody = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_MESSDATEN).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testBody))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void saveMeasure_internalErrorHappen_returnsErrorCodeInternalError() throws Exception {
    MeasureRequestDTO testRequest = new MeasureRequestDTO(72L, 20, 2, "Fruchtiger Boden", 0.04);
    ObjectMapper mapper = new ObjectMapper();
    given(measureService.save(testRequest)).willThrow(new InternalErrorException());

    MockHttpServletResponse response = mvc.perform(
            post(API_V1_MESSDATEN).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(testRequest))
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

}
