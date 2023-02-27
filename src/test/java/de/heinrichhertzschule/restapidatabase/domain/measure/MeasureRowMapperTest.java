package de.heinrichhertzschule.restapidatabase.domain.measure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.heinrichhertzschule.restapidatabase.domain.measure.MeasureRepository;
import de.heinrichhertzschule.restapidatabase.domain.measure.mapper.MeasureRowMapper;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.EnrichedMeasureDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class MeasureRowMapperTest {

  MeasureRowMapper objectUnderTest = new MeasureRowMapper();
  @Test
  public void mapRow_canParseResultSet_returnMeasureDTO()
      throws SQLException, JsonProcessingException {
    int rowNumber = 2;
    Long pid = 10L;
    Long mid = 8L;
    int x = 10;
    int y = 1;
    Double temp = 0.08;
    String ground = "FELS";
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.getLong(MeasureRepository.PID)).thenReturn(pid);
    when(resultSet.getLong(MeasureRepository.MID)).thenReturn(mid);
    when(resultSet.getInt(MeasureRepository.X)).thenReturn(x);
    when(resultSet.getInt(MeasureRepository.Y)).thenReturn(y);
    when(resultSet.getString(MeasureRepository.TYP)).thenReturn(ground);
    when(resultSet.getDouble(MeasureRepository.TEMPERATUR)).thenReturn(temp);
    ObjectMapper mapper = new ObjectMapper();
    String expected = mapper.writeValueAsString(new EnrichedMeasureDTO(mid, x, y, pid, ground, temp));

    EnrichedMeasureDTO result = objectUnderTest.mapRow(resultSet, rowNumber);

    assertEquals(expected, mapper.writeValueAsString(result));
  }

}
