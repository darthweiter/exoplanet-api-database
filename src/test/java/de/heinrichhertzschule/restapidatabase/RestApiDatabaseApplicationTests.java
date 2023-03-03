package de.heinrichhertzschule.restapidatabase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.RoboterRepository;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterInsertRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterResponseDTO;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class RestApiDatabaseApplicationTests {


  @Autowired
  private MockMvc mvc;

  @MockBean
  Statement statement;

  @MockBean
  PreparedStatement preparedStatement;

  @MockBean
  ResultSet resultSet;

  @MockBean
  Connection connection;

  @MockBean
  DataSource dataSource;

  private final static String URL = "http://localhost:8080";
  private final static String API_V1_ROBOTER = "/api/v1/roboter";
  private final static String API_V1_PLANETEN = "/api/v1/planeten";
  private final static String API_V1_MESSDATEN = "/api/v1/messdaten";

  private final static Long RTID = 1L;
  private final static String DIRECTION_DESCRIPTION = "SOUTH";

  private ObjectMapper mapper = new ObjectMapper();

  private MockHttpServletResponse response = null;


  @Test
  void contextLoads(ApplicationContext context) {
    assertThat(context).isNotNull();
  }

  @Test
  void testEndpoint_GetApiV1PlanetenSuccessfully_ReturnCodeOKWithExpectedValues() throws Exception {
    long pid = 99L;
    String planetName = "TestPlanet";
    int width = 10;
    int height = 10;
    String expected = mapper.writeValueAsString(List.of(new PlanetResponseDTO(pid, planetName, width, height)));


    given_a_database_connection();
    given_a_planet_in_database(pid, planetName, width, height);

    when_get_request_to_url_$(URL + API_V1_PLANETEN);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);
  }

  @Test
  void testEndpoint_GetApiV1PlanetenSuccessfully_ReturnCodeOKWithEmptyList() throws Exception {
    String expected = mapper.writeValueAsString(Collections.emptyList());

    given_a_database_connection();
    given_database_result_not_found();

    when_get_request_to_url_$(URL + API_V1_PLANETEN);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);
  }

  @Test
  void testEndpoint_GetApiV1PlanetenByIdSuccessfully_ReturnCodeOKWithExpectedValues() throws Exception {
    long pid = 80L;
    String planetName = "TestPlanet2";
    int width = 11;
    int height = 11;
    String expected = mapper.writeValueAsString(new PlanetResponseDTO(pid, planetName, width, height));

    given_a_database_connection();
    given_a_planet_in_database(pid, planetName, width, height);

    when_get_request_to_url_$(URL + API_V1_PLANETEN + "/" + pid);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);

  }

  @Test
  void testEndpoint_GetApiV1PlanetenByIdNotFound_ReturnCodeNotFound() throws Exception {
    long pid = 80L;

    given_a_database_connection();
    given_database_result_not_found();

    when_get_request_to_url_$(URL + API_V1_PLANETEN + "/" + pid);

    then_expect_http_status_code_not_found();
  }

  @Test
  void testEndpoint_GetApiV1PlanetenByIdError_ReturnCodeInternalServerError() throws Exception {
    long pid = 80L;

    given_a_database_connection();
    given_database_operation_will_throw_DataAccessException();

    when_get_request_to_url_$(URL + API_V1_PLANETEN + "/" + pid);

    then_expect_http_status_code_internal_server_error();
  }

  @Test
  void testEndpoint_PostApiV1PlanetenSuccessfully_returnCodeOkWithExpectedValues() throws Exception {
    Random random = new Random();
    long id = random.nextLong();
    String name = "Test2Planet";
    int height = 1;
    int width = 9;
    String jsonBody = mapper.writeValueAsString(new PlanetRequestDTO(name, width, height));
    String expected = mapper.writeValueAsString(new PlanetResponseDTO(id, name, width, height));

    given_a_database_connection();
    given_a_planet_in_database(id, name, width, height);

    when_post_request_to_url_$_with_body(URL + API_V1_PLANETEN, jsonBody);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);
  }

  @Test
  void testEndpoint_PostApiV1PlanetenDataAccessException_returnCodeBadRequest() throws Exception {
    String name = "Test2Planet";
    int height = 1;
    int width = 9;
    String jsonBody = mapper.writeValueAsString(new PlanetRequestDTO(name, width, height));

    given_a_database_connection();
    given_database_operation_will_throw_DataAccessException();

    when_post_request_to_url_$_with_body(URL + API_V1_PLANETEN, jsonBody);

    then_expect_http_status_code_bad_request();
  }

  @Test
  void testEndpoint_PostApiV1PlanetenBadBody_returnCodeBadRequest() throws Exception {
    Map<String, String> body = new HashMap<>();
    body.put("test", "test");
    String jsonBody = mapper.writeValueAsString(body);

    given_a_database_connection();
    given_database_result_not_found();

    when_post_request_to_url_$_with_body(URL + API_V1_PLANETEN, jsonBody);

    then_expect_http_status_code_bad_request();
  }

  @Test
  void testEndpoint_GetApiV1RoboterSuccessfully_ReturnCodeOKWithExpectedValues() throws Exception {
    long id = 88L;
    Long kid = 90L;
    Integer x = 1;
    Integer y = 2;
    long pid = 829L;
    long rtid = 2L;
    String direction = "EAST";
    String name = "Roboter1";
    double energy = 92.0;
    double temperature = 100.00;
    long sid = 1L;
    String status = "WORKING";
    boolean heater = true;
    boolean cooler = false;
    String expected = mapper.writeValueAsString(List.of(new RoboterResponseDTO(id, pid, x, y, name, temperature, energy, direction, status, heater, cooler)));

    given_a_database_connection();
    given_a_robot_in_database(id, kid, x, y, pid, rtid, direction, name, energy, temperature, sid, status, true, false);

    when_get_request_to_url_$(URL + API_V1_ROBOTER);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);
  }

  @Test
  void testEndpoint_GetApiV1RoboterSuccessfully_ReturnCodeOKWithEmptyList() throws Exception {
    String expected = mapper.writeValueAsString(Collections.emptyList());

    given_a_database_connection();
    given_database_result_not_found();

    when_get_request_to_url_$(URL + API_V1_ROBOTER);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);

  }

  @Test
  void testEndpoint_GetApiV1RoboterByIdNotFound_ReturnCodeNotFound() throws Exception {
    long rid = 54L;
    given_a_database_connection();
    given_database_result_not_found();

    when_get_request_to_url_$(URL + API_V1_ROBOTER + "/" + rid);

    then_expect_http_status_code_not_found();
  }

  @Test
  void testEndpoint_GetApiV1RoboterByIdError_ReturnCodeInternalError() throws Exception {
    long rid = 54L;
    given_a_database_connection();
    given_database_operation_will_throw_DataAccessException();

    when_get_request_to_url_$(URL + API_V1_ROBOTER + "/" + rid);

    then_expect_http_status_code_internal_server_error();
  }

  @Test
  void testEndpoint_PostApiV1RoboterSuccessfully_returnCodeOkWithExpectedValues() throws Exception {
    Random random = new Random();
    long id = random.nextLong();
    long pid = 19L;
    String name = "Roboter";
    String jsonBody = mapper.writeValueAsString(new RoboterInsertRequestDTO(pid, name));
    String expected = mapper.writeValueAsString(new RoboterResponseDTO(id, pid, null, null, name, 0.0, 100.0, null, "WORKING", false, false));

    given_a_database_connection();
    given_a_robot_in_database(id, null, null, null, pid, null, null, name, 100.0, 0.0, 2L, "WORKING", false, false);

    when_post_request_to_url_$_with_body(URL + API_V1_ROBOTER, jsonBody);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);
  }


  @Test
  void testEndpoint_PostApiV1RoboterDataAccessError_returnCodeBadRequest() throws Exception {
    long pid = 19L;
    String name = "Roboter";
    String jsonBody = mapper.writeValueAsString(new RoboterInsertRequestDTO(pid, name));

    given_a_database_connection();
    given_database_operation_will_throw_DataAccessException();

    when_post_request_to_url_$_with_body(URL + API_V1_ROBOTER, jsonBody);
    then_expect_http_status_code_bad_request();
  }

  @Test
  void testEndpoint_PostApiV1RoboterBadBody_returnCodeBadRequest() throws Exception {
    Map<String, String> body = new HashMap<>();
    body.put("test", "test");
    String jsonBody = mapper.writeValueAsString(body);

    given_a_database_connection();
    given_database_result_not_found();

    when_post_request_to_url_$_with_body(URL + API_V1_ROBOTER, jsonBody);

    then_expect_http_status_code_bad_request();
  }

  @Test
  void testEndpoint_PutApiV1RoboterSuccessfully_returnCodeOkWithExpectedValues() throws Exception {
    long rid = 29L;
    long pid = 19L;
    int x = 1;
    int y = 1;
    String direction = "EAST";
    String name = "Tester2";
    double energy = 0.2;
    double temperature = 9.0;
    String status = "CRASHED";
    boolean heater = true;
    boolean cooler = true;
    String jsonBody = mapper.writeValueAsString(new RoboterRequestDTO(x, y, pid, direction, name, energy, temperature, status, heater, heater));
    String expected = mapper.writeValueAsString(new RoboterResponseDTO(rid, pid, x, y, name, temperature, energy, direction, status, heater, cooler));

    given_a_database_connection();
    given_a_robot_in_database(rid, 5L, x, y, pid, 1L, direction, name, energy, temperature, 2L, status, heater, cooler);

    when_put_request_to_url_$_with_body(URL + API_V1_ROBOTER + "/" + rid, jsonBody);

    then_expect_http_status_code_ok();
    then_content_is_identical_to_$(expected);
  }

  @Test
  void testEndpoint_PutApiV1RoboterBadBody_returnCodeBadRequest() throws Exception {
    Map<String, String> body = new HashMap<>();
    body.put("test", "test");
    String jsonBody = mapper.writeValueAsString(body);
    long rid = 29L;

    given_a_database_connection();
    given_database_result_not_found();

    when_put_request_to_url_$_with_body(URL + API_V1_ROBOTER + "/" + rid, jsonBody);

    then_expect_http_status_code_bad_request();
  }

  @Test
  void testEndpoint_PutApiV1NotFound_returnCodeBadRequest() throws Exception {
    long rid = 29L;
    long pid = 19L;
    String jsonBody = mapper.writeValueAsString(new RoboterRequestDTO(1, 2, pid, "WEST", "KeinAbel", 0.0, 0.0, "CRASHED", false, false));


    given_a_database_connection();
    given_database_result_not_found();

    when_put_request_to_url_$_with_body(URL + API_V1_ROBOTER + "/" + rid, jsonBody);

    then_expect_http_status_code_bad_request();
  }

  @Test
  void testEndpoint_PutApiV1Error_returnCodeBadRequest() throws Exception {
    long rid = 29L;
    long pid = 19L;
    String jsonBody = mapper.writeValueAsString(new RoboterRequestDTO(1, 2, pid, "WEST", "KeinAbel", 0.0, 0.0, "CRASHED", false, false));


    given_a_database_connection();
    given_database_operation_will_throw_DataAccessException();

    when_put_request_to_url_$_with_body(URL + API_V1_ROBOTER + "/" + rid, jsonBody);

    then_expect_http_status_code_bad_request();
  }

  private void when_get_request_to_url_$(String url) throws Exception {
    response = mvc.perform(
            get(url).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
  }

  private void when_post_request_to_url_$_with_body(String url, String jsonBody) throws Exception {
    response = mvc.perform(
            post(url).contentType(MediaType.APPLICATION_JSON).content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
  }

  private void when_put_request_to_url_$_with_body(String url, String jsonBody) throws Exception {
    response = mvc.perform(
            put(url).contentType(MediaType.APPLICATION_JSON).content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();
  }

  private void then_expect_http_status_code_ok() {
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  private void then_expect_http_status_code_internal_server_error() {
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
  }

  private void then_expect_http_status_code_bad_request() {
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  private void then_expect_http_status_code_not_found() {
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  private void then_content_is_identical_to_$(String json) throws UnsupportedEncodingException {
    assertEquals(json, response.getContentAsString());
  }

  private void given_a_planet_in_database(long id, String name, int width, int height)
      throws SQLException {
    given(resultSet.getLong(PlanetRepository.PID)).willReturn(id);
    given(resultSet.getString(PlanetRepository.NAME)).willReturn(name);
    given(resultSet.getInt(PlanetRepository.BREITE)).willReturn(width);
    given(resultSet.getInt(PlanetRepository.HOEHE)).willReturn(height);
    given(resultSet.next()).willReturn(true).willReturn(false);
    given(preparedStatement.executeQuery()).willReturn(resultSet);
    given(preparedStatement.executeUpdate()).willReturn(1);
    given(statement.executeQuery(any())).willReturn(resultSet);
  }

  private void given_database_result_not_found() throws SQLException {
    given(preparedStatement.executeQuery()).willThrow(EmptyResultDataAccessException.class);
    given(statement.executeQuery(any())).willThrow(EmptyResultDataAccessException.class);
    given(preparedStatement.executeUpdate()).willReturn(0);
  }

  private void given_database_operation_will_throw_DataAccessException() throws SQLException {
    given(preparedStatement.executeQuery()).willThrow(new TestDataAccessExceptionImpl());
    given(statement.executeQuery(any())).willThrow(new TestDataAccessExceptionImpl());
    given(preparedStatement.executeUpdate()).willThrow(new TestDataAccessExceptionImpl());
  }

  private void given_a_robot_in_database(long id, Long kid, Integer x, Integer y, long pid, Long rtid, String direction, String name, double energy, double temperature, long sid, String status, boolean heater, boolean cooler)
      throws SQLException {
    given(resultSet.getLong(RoboterRepository.RID)).willReturn(id);
    given(resultSet.getLong(RoboterRepository.KID)).willReturn(kid == null ? 0 : kid);
    given(resultSet.getInt(RoboterRepository.X)).willReturn(kid == null ? 0 : x);
    given(resultSet.getInt(RoboterRepository.Y)).willReturn(kid == null ? 0 : y);
    given(resultSet.getLong(RoboterRepository.PID)).willReturn(pid);
    given(resultSet.getLong(RoboterRepository.RTID)).willReturn(rtid == null ? 0 : rtid);
    given(resultSet.getString(RoboterRepository.BEZEICHNUNG)).willReturn(direction);
    given(resultSet.getString(RoboterRepository.NAME)).willReturn(name);
    given(resultSet.getDouble(RoboterRepository.ENERGY)).willReturn(energy);
    given(resultSet.getDouble(RoboterRepository.BETRIEBSTEMPERATUR)).willReturn(temperature);
    given(resultSet.getLong(RoboterRepository.SID)).willReturn(sid);
    given(resultSet.getString(RoboterRepository.STATUS)).willReturn(status);
    given(resultSet.getBoolean(RoboterRepository.HEATER)).willReturn(heater);
    given(resultSet.getBoolean(RoboterRepository.COOLER)).willReturn(cooler);
    given(resultSet.next()).willReturn(true).willReturn(false);
    given(preparedStatement.executeQuery()).willReturn(resultSet);
    given(preparedStatement.executeUpdate()).willReturn(9);
    given(statement.executeQuery(any())).willReturn(resultSet);
  }

  private void given_a_database_connection() throws SQLException {
    given(dataSource.getConnection()).willReturn(connection);
    given(connection.prepareStatement(any())).willReturn(preparedStatement);
    given(connection.createStatement()).willReturn(statement);
  }
}
