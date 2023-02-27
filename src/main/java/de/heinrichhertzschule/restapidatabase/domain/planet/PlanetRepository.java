package de.heinrichhertzschule.restapidatabase.domain.planet;

import de.heinrichhertzschule.restapidatabase.domain.planet.mapper.EnrichedPlanetRowMapper;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.EnrichedPlanetDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetRequestDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.InsertFailedException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.UpdateFailedException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.PlanetNotFoundException;
import de.heinrichhertzschule.restapidatabase.domain.planet.mapper.PlanetRowMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlanetRepository {
  public final static String PID = "PID";

  public final static String KID = "KID";
  public final static String NAME = "name";
  public final static String PLANET_NAME = "PlanetName";
  public final static String BREITE = "Breite";
  public final static String HOEHE = "Hoehe";

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final JdbcTemplate jdbcTemplate;

  public PlanetRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.jdbcTemplate = jdbcTemplate;
  }

  public void insert(String name, int width, int height) throws InternalErrorException, InsertFailedException {
    String sql = "INSERT INTO planeten (Name, Breite, Hoehe) VALUES (:name, :breite, :hoehe)";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("name", name);
    parameters.addValue("breite", width);
    parameters.addValue("hoehe", height);
    try {
      namedParameterJdbcTemplate.update(sql, parameters);
    } catch (DataAccessException exception) {
      throw new InsertFailedException(exception.getMessage());
    }
  }

  public void update(long planetId, PlanetRequestDTO planet)
      throws UpdateFailedException {
    String sql = "UPDATE planeten SET name = :name, Breite = :breite, Hoehe = :hoehe WHERE PID = :planetId";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("planetId", planetId);
    parameters.addValue("name", planet.name());
    parameters.addValue("breite", planet.width());
    parameters.addValue("hoehe", planet.height());
    try {
      namedParameterJdbcTemplate.update(sql, parameters);
    } catch (DataAccessException e) {
      throw new UpdateFailedException(e.getMessage());
    }
  }

  public PlanetDTO selectById(long planetId)
      throws PlanetNotFoundException, InternalErrorException {
    String sql = "SELECT PID, Name, Breite, Hoehe FROM planeten WHERE PID = :planetId";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("planetId", planetId);
    try{
      return namedParameterJdbcTemplate.queryForObject(sql, parameters, new PlanetRowMapper());
    } catch (EmptyResultDataAccessException e) {
      throw new PlanetNotFoundException("id: " + planetId);
    } catch (DataAccessException e) {
      throw new InternalErrorException();
    }
  }

  public PlanetDTO selectBy(PlanetRequestDTO planet)
      throws InternalErrorException, PlanetNotFoundException {
    String sql = "SELECT PID, Name, Breite, Hoehe FROM planeten WHERE Name = :name AND Breite = :breite AND Hoehe = :hoehe";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("name", planet.name());
    parameters.addValue("breite", planet.width());
    parameters.addValue("hoehe", planet.height());
    try{
      return namedParameterJdbcTemplate.queryForObject(sql, parameters, new PlanetRowMapper());
    } catch (EmptyResultDataAccessException e) {
      throw new PlanetNotFoundException(planet.toString());
    } catch (DataAccessException e) {
      throw new InternalErrorException();
    }
  }

  public List<PlanetDTO> getAll() {
    String sql = "SELECT PID, Name, Breite, Hoehe FROM planeten ORDER BY PID";
    try {
      return jdbcTemplate.query(sql, new PlanetRowMapper());
    } catch (DataAccessException e) {
      return Collections.emptyList();
    }
  }

  public List<EnrichedPlanetDTO> getPlanetFields(long pid)
      throws PlanetNotFoundException, InternalErrorException {
    String sql ="SELECT PID, PlanetName, Breite, Hoehe, KID, X, Y, MID, Typ, Temperatur, RID, Bezeichnung AS Richtung, RoboterName, Energie, Betriebstemperatur, Status, Heater, Cooler FROM (SELECT PlanetMessdaten.PID, PlanetMessdaten.KID AS MessdatenKID, PlanetMessdaten.Name AS PlanetName, PlanetMessdaten.Breite, PlanetMessdaten.Hoehe, PlanetMessdaten.MID, PlanetMessdaten.TYP, PlanetMessdaten.TEMPERATUR, RoboterData.RID, RoboterData.KID as RoboterKID, RoboterData.Bezeichnung, RoboterData.Name AS RoboterName, RoboterData.Energie, RoboterData.Betriebstemperatur, RoboterData.Status, RoboterData.Heater, RoboterData.Cooler  FROM (SELECT planeten.PID, planeten.Name, planeten.Breite, planeten.Hoehe, koordinaten.KID, koordinaten.X, koordinaten.Y, messdaten.MID, boeden.Typ, messdaten.Temperatur "
        + "FROM planeten "
        + "LEFT OUTER JOIN messdaten ON planeten.PID = messdaten.PID "
        + "LEFT OUTER JOIN koordinaten ON messdaten.KID = koordinaten.KID "
        + "LEFT OUTER JOIN boeden On boeden.BID = messdaten.BID) AS PlanetMessdaten "
        + "LEFT OUTER JOIN (SELECT roboter.RID, roboter.PID, roboter.KID, koordinaten.X, koordinaten.Y, richtungen.Bezeichnung, roboter.Name, roboter.Energie, roboter.Betriebstemperatur, stati.Status, roboter.Heater, roboter.Cooler "
        + "FROM roboter "
        + "LEFT OUTER JOIN koordinaten ON roboter.KID = koordinaten.KID "
        + "lEFT OUTER JOIN richtungen ON roboter.RTID = richtungen.RTID "
        + "LEfT OUTER JOIN stati ON roboter.SID = stati.SID) AS RoboterData ON PlanetMessdaten.PID = RoboterData.PID) AS PlanetMessdataRobots "
        + "LEFT OUTER JOIN koordinaten ON koordinaten.KID = PlanetMessdataRobots.MessdatenKID OR koordinaten.KID = PlanetMessdataRobots.RoboterKID "
        + "WHERE PID = :pid "
        + "ORDER BY PID, KID;";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("pid", pid);

    try {
      List<EnrichedPlanetDTO> enrichedPlanets = namedParameterJdbcTemplate.query(sql, parameters, new EnrichedPlanetRowMapper());
      if(enrichedPlanets.isEmpty()) {
        throw new PlanetNotFoundException("pid: " + pid);
      } else {
        return enrichedPlanets;
      }
    } catch (EmptyResultDataAccessException e) {
      throw new PlanetNotFoundException("pid: " + pid);
    } catch (DataAccessException e) {
      throw new InternalErrorException();
    }
  }



}
