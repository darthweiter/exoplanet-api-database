package de.heinrichhertzschule.restapidatabase.domain.planet.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.measure.MeasureRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.EnrichedPlanetDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.RoboterRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class EnrichedPlanetRowMapperTest {

  EnrichedPlanetRowMapper objectUnderTest = new EnrichedPlanetRowMapper();

  @Test
  public void mapRow_canParseResultSet_returnPlanetDTO()
      throws SQLException, JsonProcessingException {
    int rowNumber = 2;
    Long pid = 10L;
    Long kid = 8L;
    Integer x = 1;
    Integer y = 2;
    String ground = "MORAST";
    Double temperature = 0.02;
    Long rid = 990L;
    String direction = "WEST";
    String roboterName = "Wally";
    Double energy = 0.10;
    Double robotTemperature = -10.20;
    String status = "CRASHED";
    Boolean heater = true;
    Boolean cooler = false;
    String name = "TestPlanet";
    Integer width = 2;
    Integer height = 4;
    Long mid = 2L;
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong(PlanetRepository.PID)).thenReturn(pid);
    when(resultSet.getLong(PlanetRepository.KID)).thenReturn(kid);
    when(resultSet.getInt(RoboterRepository.X)).thenReturn(x);
    when(resultSet.getInt(RoboterRepository.Y)).thenReturn(y);
    when(resultSet.getString(PlanetRepository.PLANET_NAME)).thenReturn(name);
    when(resultSet.getInt(PlanetRepository.BREITE)).thenReturn(width);
    when(resultSet.getInt(PlanetRepository.HOEHE)).thenReturn(height);
    when(resultSet.getString(MeasureRepository.TYP)).thenReturn(ground);
    when(resultSet.getDouble(MeasureRepository.TEMPERATUR)).thenReturn(temperature);
    when(resultSet.getLong(RoboterRepository.RID)).thenReturn(rid);
    when(resultSet.getString(RoboterRepository.DIRECTION)).thenReturn(direction);
    when(resultSet.getString(RoboterRepository.ROBOTER_NAME)).thenReturn(roboterName);
    when(resultSet.getDouble(RoboterRepository.ENERGY)).thenReturn(energy);
    when(resultSet.getDouble(RoboterRepository.BETRIEBSTEMPERATUR)).thenReturn(robotTemperature);
    when(resultSet.getString(RoboterRepository.STATUS)).thenReturn(status);
    when(resultSet.getBoolean(RoboterRepository.HEATER)).thenReturn(heater);
    when(resultSet.getBoolean(RoboterRepository.COOLER)).thenReturn(cooler);
    when(resultSet.getLong(MeasureRepository.MID)).thenReturn(mid);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(new EnrichedPlanetDTO(pid, kid, x, y, name, width, height, mid , ground, temperature, rid, direction, roboterName, energy, robotTemperature, status, heater, cooler));

    EnrichedPlanetDTO result = objectUnderTest.mapRow(resultSet, rowNumber);

    assertEquals(expected, mapper.writeValueAsString(result));
  }

  @Test
  public void mapRow_midIsNull_returnEnrichedPlanetDTOWithoutMessdata()
      throws SQLException, JsonProcessingException {
    int rowNumber = 2;
    Long pid = 10L;
    Long kid = 8L;
    Integer x = 1;
    Integer y = 2;
    Long rid = 990L;
    String direction = "WEST";
    String roboterName = "Wally";
    Double energy = 0.10;
    Double robotTemperature = -10.20;
    String status = "CRASHED";
    Boolean heater = true;
    Boolean cooler = false;
    String name = "TestPlanet";
    Integer width = 2;
    Integer height = 4;
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong(PlanetRepository.PID)).thenReturn(pid);
    when(resultSet.getLong(PlanetRepository.KID)).thenReturn(kid);
    when(resultSet.getInt(RoboterRepository.X)).thenReturn(x);
    when(resultSet.getInt(RoboterRepository.Y)).thenReturn(y);
    when(resultSet.getString(PlanetRepository.PLANET_NAME)).thenReturn(name);
    when(resultSet.getInt(PlanetRepository.BREITE)).thenReturn(width);
    when(resultSet.getInt(PlanetRepository.HOEHE)).thenReturn(height);
    when(resultSet.getLong(RoboterRepository.RID)).thenReturn(rid);
    when(resultSet.getString(RoboterRepository.DIRECTION)).thenReturn(direction);
    when(resultSet.getString(RoboterRepository.ROBOTER_NAME)).thenReturn(roboterName);
    when(resultSet.getDouble(RoboterRepository.ENERGY)).thenReturn(energy);
    when(resultSet.getDouble(RoboterRepository.BETRIEBSTEMPERATUR)).thenReturn(robotTemperature);
    when(resultSet.getString(RoboterRepository.STATUS)).thenReturn(status);
    when(resultSet.getBoolean(RoboterRepository.HEATER)).thenReturn(heater);
    when(resultSet.getBoolean(RoboterRepository.COOLER)).thenReturn(cooler);
    when(resultSet.getLong(MeasureRepository.MID)).thenReturn(0L);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(new EnrichedPlanetDTO(pid, kid, x, y, name, width, height, null, null, null, rid, direction, roboterName, energy, robotTemperature, status, heater, cooler));

    EnrichedPlanetDTO result = objectUnderTest.mapRow(resultSet, rowNumber);

    assertEquals(expected, mapper.writeValueAsString(result));
  }

  @Test
  public void mapRow_ridIsNull_returnsEnrichedPlanetWithoutRoboterData()
      throws SQLException, JsonProcessingException {
    int rowNumber = 2;
    Long pid = 10L;
    Long kid = 8L;
    Integer x = 1;
    Integer y = 2;
    String ground = "MORAST";
    Double temperature = 0.02;
    String name = "TestPlanet";
    Integer width = 2;
    Integer height = 4;
    Long mid = 2L;
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong(PlanetRepository.PID)).thenReturn(pid);
    when(resultSet.getLong(PlanetRepository.KID)).thenReturn(kid);
    when(resultSet.getInt(RoboterRepository.X)).thenReturn(x);
    when(resultSet.getInt(RoboterRepository.Y)).thenReturn(y);
    when(resultSet.getString(PlanetRepository.PLANET_NAME)).thenReturn(name);
    when(resultSet.getInt(PlanetRepository.BREITE)).thenReturn(width);
    when(resultSet.getInt(PlanetRepository.HOEHE)).thenReturn(height);
    when(resultSet.getString(MeasureRepository.TYP)).thenReturn(ground);
    when(resultSet.getDouble(MeasureRepository.TEMPERATUR)).thenReturn(temperature);
    when(resultSet.getLong(RoboterRepository.RID)).thenReturn(0L);
    when(resultSet.getLong(MeasureRepository.MID)).thenReturn(mid);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(new EnrichedPlanetDTO(pid, kid, x, y, name, width, height, mid, ground, temperature, null, null, null, null, null, null, null, null));

    EnrichedPlanetDTO result = objectUnderTest.mapRow(resultSet, rowNumber);

    assertEquals(expected, mapper.writeValueAsString(result));
  }

  @Test
  public void mapRow_kidIsNull_returnsEnrichedPlanetWithoutRoboterData()
      throws SQLException, JsonProcessingException {
    int rowNumber = 2;
    Long pid = 10L;
    String ground = "MORAST";
    Double temperature = 0.02;
    String name = "TestPlanet";
    Integer width = 2;
    Integer height = 4;
    Long mid = 2L;
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong(PlanetRepository.PID)).thenReturn(pid);
    when(resultSet.getLong(PlanetRepository.KID)).thenReturn(0L);
    when(resultSet.getString(PlanetRepository.PLANET_NAME)).thenReturn(name);
    when(resultSet.getInt(PlanetRepository.BREITE)).thenReturn(width);
    when(resultSet.getInt(PlanetRepository.HOEHE)).thenReturn(height);
    when(resultSet.getString(MeasureRepository.TYP)).thenReturn(ground);
    when(resultSet.getDouble(MeasureRepository.TEMPERATUR)).thenReturn(temperature);
    when(resultSet.getLong(RoboterRepository.RID)).thenReturn(0L);
    when(resultSet.getLong(MeasureRepository.MID)).thenReturn(mid);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(new EnrichedPlanetDTO(pid, null, null, null, name, width, height, mid, ground, temperature, null, null, null, null, null, null, null, null));

    EnrichedPlanetDTO result = objectUnderTest.mapRow(resultSet, rowNumber);

    assertEquals(expected, mapper.writeValueAsString(result));
  }

}
