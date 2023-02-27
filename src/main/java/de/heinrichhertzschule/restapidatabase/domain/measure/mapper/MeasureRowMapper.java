package de.heinrichhertzschule.restapidatabase.domain.measure.mapper;

import de.heinrichhertzschule.restapidatabase.domain.measure.MeasureRepository;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.EnrichedMeasureDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MeasureRowMapper implements RowMapper<EnrichedMeasureDTO> {

  @Override
  public EnrichedMeasureDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new EnrichedMeasureDTO(
        rs.getLong(MeasureRepository.MID),
        rs.getInt(MeasureRepository.X),
        rs.getInt(MeasureRepository.Y),
        rs.getLong(MeasureRepository.PID),
        rs.getString(MeasureRepository.TYP),
        rs.getDouble(MeasureRepository.TEMPERATUR));
  }
}
