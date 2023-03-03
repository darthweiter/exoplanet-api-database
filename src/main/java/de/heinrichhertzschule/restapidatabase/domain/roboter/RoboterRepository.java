package de.heinrichhertzschule.restapidatabase.domain.roboter;

import de.heinrichhertzschule.restapidatabase.domain.roboter.mapper.RoboterRowMapper;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.EnrichedRoboterDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterInsertRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterRequestDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.InsertFailedException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.UpdateFailedException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.RoboterNotFoundException;
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
public class RoboterRepository {

  private final static Logger logger = LoggerFactory.getLogger(RoboterRepository.class);
  public final static String RID = "rid";
  public final static String KID = "kid";
  public final static String ROBOTER_KID = "RoboterKID";
  public final static String PID = "pid";
  public final static String RTID = "rtid";
  public final static String BEZEICHNUNG = "Bezeichnung";
  public final static String DIRECTION = "Richtung";
  public final static String NAME = "name";
  public final static String ROBOTER_NAME = "RoboterName";
  public final static String ENERGY = "Energie";
  public final static String BETRIEBSTEMPERATUR = "Betriebstemperatur";
  public final static String SID = "sid";
  public static final String STATUS = "status";
  public static final String X = "X";
  public static final String Y = "Y";
  public static final String HEATER = "Heater";
  public static final String COOLER = "Cooler";

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final JdbcTemplate jdbcTemplate;

  public RoboterRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
      JdbcTemplate jdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.jdbcTemplate = jdbcTemplate;
  }

  public void insert(RoboterInsertRequestDTO roboter)
      throws InsertFailedException, InternalErrorException {
    String sql = "INSERT INTO roboter (PID, Name) VALUES(:pid, :name)";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("pid", roboter.pid());
    parameters.addValue("name", roboter.name());
    try {
      int effectedRows = namedParameterJdbcTemplate.update(sql, parameters);
      if(effectedRows == 0) {
        logger.warn("No effected Rows - " + roboter);
        throw new InsertFailedException("body: " + roboter);
      }
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new InsertFailedException(e.getMessage());
    }
  }

  public void update(long id, RoboterRequestDTO input)
      throws UpdateFailedException {
    String sql = "CALL update_roboter(:rid, :pid, :x, :y, :direction, :name, :energy, :temperature, :status, :heater, :cooler);";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("rid", id);
    parameters.addValue("pid", input.pid());
    parameters.addValue("x", input.x());
    parameters.addValue("y", input.y());
    parameters.addValue("direction", input.direction());
    parameters.addValue("name", input.name());
    parameters.addValue("energy", input.energy());
    parameters.addValue("temperature", input.temperature());
    parameters.addValue("status", input.status());
    parameters.addValue("heater", input.heater());
    parameters.addValue("cooler", input.cooler());
    try {
      int effectedRows = namedParameterJdbcTemplate.update(sql, parameters);
      if(effectedRows <= 8) {
        throw new UpdateFailedException("rid: " + id + " body: " + input.toString());
      }
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new UpdateFailedException(e.getMessage());
    }
  }

  public List<EnrichedRoboterDTO> getAll() {
    String sql =
        "SELECT roboter.RID, roboter.KID, koordinaten.x, koordinaten.Y, roboter.PID, roboter.RTID, richtungen.Bezeichnung, roboter.Name, roboter.Energie, roboter.Betriebstemperatur, roboter.SID, stati.Status, roboter.Heater, roboter.Cooler"
            + "  FROM roboter "
            + "  LEFT OUTER JOIN koordinaten ON roboter.KID = koordinaten.KID "
            + "  LEFT OUTER JOIN richtungen ON roboter.RTID = richtungen.RTID "
            + "  INNER JOIN stati ON roboter.SID = stati.SID;";
    try {
      return jdbcTemplate.query(sql, new RoboterRowMapper());
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  public EnrichedRoboterDTO getByPlanetIdAndName(long pid, String name)
      throws RoboterNotFoundException, InternalErrorException {
    String sql =
        "SELECT roboter.RID, roboter.KID, koordinaten.x, koordinaten.Y, roboter.PID, roboter.RTID, richtungen.Bezeichnung, roboter.Name, roboter.Energie, roboter.Betriebstemperatur, roboter.SID, stati.Status, roboter.Heater, roboter.Cooler"
            + "  FROM roboter "
            + "  LEFT OUTER JOIN koordinaten ON roboter.KID = koordinaten.KID "
            + "  LEFT OUTER JOIN richtungen ON roboter.RTID = richtungen.RTID "
            + "  INNER JOIN stati ON roboter.SID = stati.SID WHERE PID = :pid AND Name = :name;";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("pid", pid);
    parameters.addValue("name", name);
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, parameters, new RoboterRowMapper());
    } catch (EmptyResultDataAccessException e) {
      logger.warn("NotFoundException - pid: " + pid + "roboterName: " + name);
      throw new RoboterNotFoundException("pid: " + pid + " name: " + name);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new InternalErrorException();
    }
  }

  public EnrichedRoboterDTO getById(long id)
      throws RoboterNotFoundException, InternalErrorException {
    String sql =
        "SELECT roboter.RID, roboter.KID, koordinaten.x, koordinaten.Y, roboter.PID, roboter.RTID, richtungen.Bezeichnung, roboter.Name, roboter.Energie, roboter.Betriebstemperatur, roboter.SID, stati.Status, roboter.Heater, roboter.Cooler"
            + "  FROM roboter "
            + "  LEFT OUTER JOIN koordinaten ON roboter.KID = koordinaten.KID "
            + "  LEFT OUTER JOIN richtungen ON roboter.RTID = richtungen.RTID "
            + "  INNER JOIN stati ON roboter.SID = stati.SID WHERE RID = :rid";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("rid", id);
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, parameters, new RoboterRowMapper());
    } catch (EmptyResultDataAccessException e) {
      logger.warn("NotFoundException - id : " + id);
      throw new RoboterNotFoundException(String.valueOf(id));
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new InternalErrorException();
    }
  }
}
