package de.heinrichhertzschule.restapidatabase.domain.roboter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.roboter.RoboterRepository;
import de.heinrichhertzschule.restapidatabase.domain.roboter.mapper.RoboterRowMapper;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.EnrichedRoboterDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class RoboterRowMapperTest {

  RoboterRowMapper objectUnderTest = new RoboterRowMapper();

  @Test
  public void mapRow_canParseResultSet_returnMeasureDTO()
      throws SQLException, JsonProcessingException {
    int rowNumber = 2;
    Long pid = 10L;
    Double temp = 0.00;
    Long rid = 29L;
    String name = "RoboterTestName";
    double energy = 100.00;
    Long sid = 9233L;
    String status = "Stati";
    boolean heater = true;
    boolean cooler = false;

    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong(RoboterRepository.KID)).thenReturn(0L);
    when(resultSet.getLong(RoboterRepository.PID)).thenReturn(pid);
    when(resultSet.getLong(RoboterRepository.RTID)).thenReturn(0L);
    when(resultSet.getLong(RoboterRepository.SID)).thenReturn(sid);
    when(resultSet.getString(RoboterRepository.STATUS)).thenReturn(status);
    when(resultSet.getDouble(RoboterRepository.BETRIEBSTEMPERATUR)).thenReturn(temp);
    when(resultSet.getLong(RoboterRepository.RID)).thenReturn(rid);
    when(resultSet.getString(RoboterRepository.NAME)).thenReturn(name);
    when(resultSet.getDouble(RoboterRepository.ENERGY)).thenReturn(energy);
    when(resultSet.getBoolean(RoboterRepository.HEATER)).thenReturn(heater);
    when(resultSet.getBoolean(RoboterRepository.COOLER)).thenReturn(cooler);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(
        new EnrichedRoboterDTO(rid, null, null, null, pid, null, null, name, energy, temp, sid,
            status, heater, cooler));

    EnrichedRoboterDTO result = objectUnderTest.mapRow(resultSet, rowNumber);

    assertEquals(expected, mapper.writeValueAsString(result));
  }

}
