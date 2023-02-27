package de.heinrichhertzschule.restapidatabase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.mapper.PlanetRowMapper;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.mapper.RoboterRowMapper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
  ResultSet resultSet;

  @MockBean
  Connection connection;

  @MockBean
  DataSource dataSource;

//  @InjectMocks
//  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
  @InjectMocks
  private JdbcTemplate jdbcTemplate;

  private final static String URL = "http://localhost:8080";
  private final static String API_V1_ROBOTER = "/api/v1/roboter";
  private final static String API_V1_PLANETEN = "/api/v1/planeten";
  private final static String API_V1_MESSDATEN = "/api/v1/messdaten";

  private final static Long RTID = 1L;
  private final static String DIRECTION_DESCRIPTION = "SOUTH";

  private ObjectMapper mapper = new ObjectMapper();


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
    given(connection.createStatement()).willReturn(statement);
    given(dataSource.getConnection()).willReturn(connection);
    given(resultSet.getLong(PlanetRepository.PID)).willReturn(pid);
    given(resultSet.getString(PlanetRepository.NAME)).willReturn(planetName);
    given(resultSet.getInt(PlanetRepository.BREITE)).willReturn(width);
    given(resultSet.getInt(PlanetRepository.HOEHE)).willReturn(height);
    given(resultSet.next()).willReturn(true).willReturn(false);
    given(statement.executeQuery("SELECT PID, Name, Breite, Hoehe FROM planeten ORDER BY PID")).willReturn(resultSet);

    MockHttpServletResponse response = mvc.perform(
            get(URL + API_V1_PLANETEN).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andReturn().getResponse();

    List<PlanetResponseDTO> expectedValue = List.of(new PlanetResponseDTO(pid, planetName, width, height));
    String expected = mapper.writeValueAsString(expectedValue);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(expected, response.getContentAsString());
  }

}
