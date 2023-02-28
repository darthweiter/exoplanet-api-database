package de.heinrichhertzschule.restapidatabase.domain.planet.mapper;

import de.heinrichhertzschule.restapidatabase.domain.measure.MeasureRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.PlanetRepository;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.EnrichedPlanetDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.RoboterRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EnrichedPlanetRowMapper implements RowMapper<EnrichedPlanetDTO> {

  @Override
  public EnrichedPlanetDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
    //planet Data
    long pid = rs.getLong(PlanetRepository.PID);
    String planetName = rs.getString(PlanetRepository.PLANET_NAME);
    int width = rs.getInt(PlanetRepository.BREITE);
    int height = rs.getInt(PlanetRepository.HOEHE);
    Integer x = rs.getInt(MeasureRepository.X);
    Integer y = rs.getInt(MeasureRepository.Y);
    //coordinate
    Long kid = rs.getLong(PlanetRepository.KID);
    if(kid == 0) {
      kid = null;
      x = null;
      y = null;
    }

    //measure
    String ground = rs.getString(MeasureRepository.TYP);
    Double temperature = rs.getDouble(MeasureRepository.TEMPERATUR);
    Long mid = rs.getLong(MeasureRepository.MID);
    if(mid == 0) {
      mid = null;
      ground = null;
      temperature = null;
    }
//robot
    String direction = rs.getString(RoboterRepository.DIRECTION);
    String roboterName = rs.getString(RoboterRepository.ROBOTER_NAME);
    Double energy = rs.getDouble(RoboterRepository.ENERGY);
    Double robotTemp = rs.getDouble(RoboterRepository.BETRIEBSTEMPERATUR);
    String status = rs.getString(RoboterRepository.STATUS);
    Boolean heater = rs.getBoolean(RoboterRepository.HEATER);
    Boolean cooler = rs.getBoolean(RoboterRepository.COOLER);
    Long rid = rs.getLong(RoboterRepository.RID);
    if(rid == 0) {
      rid = null;
      direction = null;
      roboterName = null;
      energy = null;
      robotTemp = null;
      status = null;
      heater = null;
      cooler = null;
    }

    return new EnrichedPlanetDTO(pid, kid, x, y, planetName,  width, height, mid, ground, temperature, rid, direction, roboterName, energy, robotTemp, status, heater, cooler);
  }
}
