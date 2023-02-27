package de.heinrichhertzschule.restapidatabase.domain.planet.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.mapper.PlanetRowMapper;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class PlanetRowMapperTest {

  PlanetRowMapper objectUnderTest = new PlanetRowMapper();

  @Test
  public void mapRow_canParseResultSet_returnPlanetDTO()
      throws SQLException, JsonProcessingException {
    int rowNumber = 2;
    Long pid = 10L;
    String name = "TestPlanet";
    Integer width = 2;
    Integer height = 4;
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong(PlanetRepository.PID)).thenReturn(pid);
    when(resultSet.getString(PlanetRepository.NAME)).thenReturn(name);
    when(resultSet.getInt(PlanetRepository.BREITE)).thenReturn(width);
    when(resultSet.getInt(PlanetRepository.HOEHE)).thenReturn(height);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(new PlanetDTO(pid, name, width, height));

    PlanetDTO result = objectUnderTest.mapRow(resultSet, rowNumber);

    assertEquals(expected, mapper.writeValueAsString(result));
  }
}