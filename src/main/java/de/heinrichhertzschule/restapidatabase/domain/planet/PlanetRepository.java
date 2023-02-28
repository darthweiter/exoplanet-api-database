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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlanetRepository {

  private final static Logger logger = LoggerFactory.getLogger(PlanetRepository.class);
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
      logger.error(exception.getMessage());
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
      logger.error(e.getMessage());
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
      logger.warn("NotFoundException - Planet with id: " + planetId);
      throw new PlanetNotFoundException("id: " + planetId);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
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
      logger.warn("NotFoundException - planet: " + planet.toString());
      throw new PlanetNotFoundException(planet.toString());
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new InternalErrorException();
    }
  }

  public List<PlanetDTO> getAll() {
    String sql = "SELECT PID, Name, Breite, Hoehe FROM planeten ORDER BY PID";
    try {
      return jdbcTemplate.query(sql, new PlanetRowMapper());
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  public List<EnrichedPlanetDTO> getPlanetFields(long pid)
      throws PlanetNotFoundException, InternalErrorException {
    String sql ="SELECT PlanetCoordData.PID, planeten.Name AS PlanetName, Breite, Hoehe, PlanetCoordData.KID, koordinaten.X, koordinaten.Y, PlanetCoordData.MID, PlanetCoordData.Typ, PlanetCoordData.Temperatur,PlanetCoordData.RID, PlanetCoordData.Bezeichnung AS Richtung, PlanetCoordData.Name AS RoboterName, Energie, Betriebstemperatur, Status, Heater, Cooler FROM (SELECT :pid AS PID, Coord.KID, MID, Messdata.Typ, Messdata.Temperatur, RID, Bezeichnung, Name, Energie, Betriebstemperatur, Status, Heater, Cooler "
        + "FROM (SELECT KID FROM koordinaten WHERE X < (SELECT Breite FROM planeten WHERE PID = :pid) AND Y < (SELECT Hoehe FROM planeten WHERE PID = :pid)) AS Coord "
        + "LEFT OUTER JOIN ( "
        + "    SELECT messdaten.MID, messdaten.KID, boeden.Typ, messdaten.Temperatur FROM messdaten INNER JOIN boeden ON messdaten.BID = boeden.BID WHERE KID IN ( "
        + "        SELECT KID FROM koordinaten WHERE X < (SELECT Breite FROM planeten WHERE PID = :pid) AND Y < (SELECT Hoehe FROM planeten WHERE PID = :pid)) AND PID = :pid) AS Messdata ON Coord.KID = Messdata.KID "
        + "LEFT OUTER JOIN ( "
        + "    SELECT roboter.RID, roboter.KID, richtungen.Bezeichnung, roboter.Name, roboter.Energie, roboter.Betriebstemperatur, stati.Status, roboter.Heater, roboter.Cooler FROM roboter INNER JOIN richtungen ON roboter.RTID = richtungen.RTID INNER JOIN stati ON roboter.SID = stati.SID WHERE KID IN ( "
        + "        SELECT KID FROM koordinaten WHERE X < (SELECT Breite FROM planeten WHERE PID = :pid) AND Y < (SELECT Hoehe FROM planeten WHERE PID = :pid)) AND PID = :pid) AS Robotdata ON Coord.KID = Robotdata.KID "
        + ") AS PlanetCoordData "
        + "INNER JOIN koordinaten ON PlanetCoordData.KID = koordinaten.KID "
        + "INNER JOIN planeten ON PlanetCoordData.PID = planeten.PID;";
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
      logger.error(e.getMessage());
      throw new InternalErrorException();
    }
  }



}
