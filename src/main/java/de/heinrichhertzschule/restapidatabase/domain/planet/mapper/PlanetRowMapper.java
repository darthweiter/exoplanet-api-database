package de.heinrichhertzschule.restapidatabase.domain.planet.mapper;

import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PlanetRowMapper implements RowMapper<PlanetDTO> {

  @Override
  public PlanetDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new PlanetDTO(
        rs.getLong(PlanetRepository.PID),
        rs.getString(PlanetRepository.NAME),
        rs.getInt(PlanetRepository.BREITE),
        rs.getInt(PlanetRepository.HOEHE));
  }
}
