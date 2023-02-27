package de.heinrichhertzschule.restapidatabase.domain.roboter.mapper;

import de.heinrichhertzschule.restapidatabase.domain.roboter.RoboterRepository;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.EnrichedRoboterDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RoboterRowMapper implements RowMapper<EnrichedRoboterDTO> {

  @Override
  public EnrichedRoboterDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

    Long kid = rs.getLong(RoboterRepository.KID);
    Integer X = rs.getInt(RoboterRepository.X);
    Integer Y = rs.getInt(RoboterRepository.Y);
    if(kid == 0) {
      kid = null;
      X = null;
      Y = null;
    }
    Long rtid = rs.getLong(RoboterRepository.RTID);
    String Bezeichnung = rs.getString(RoboterRepository.BEZEICHNUNG);
    if(rtid == 0) {
      rtid = null;
      Bezeichnung = null;
    }

    return new EnrichedRoboterDTO(rs.getLong(RoboterRepository.RID), kid, X, Y,
        rs.getLong(RoboterRepository.PID), rtid, Bezeichnung, rs.getString(RoboterRepository.NAME),
        rs.getDouble(RoboterRepository.ENERGY), rs.getDouble(RoboterRepository.BETRIEBSTEMPERATUR),
        rs.getLong(RoboterRepository.SID), rs.getString(RoboterRepository.STATUS), rs.getBoolean(RoboterRepository.HEATER), rs.getBoolean(RoboterRepository.COOLER));
  }
}
