package de.heinrichhertzschule.restapidatabase.domain.measure;

import de.heinrichhertzschule.restapidatabase.domain.measure.model.EnrichedMeasureDTO;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureRequestDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.UpdateFailedException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.MeasureNotFoundException;
import de.heinrichhertzschule.restapidatabase.domain.measure.mapper.MeasureRowMapper;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MeasureRepository {
  public static String MID = "mid";
  public static String MESSDATEN_KID = "MessdatenKID";
  public static String X = "X";
  public static String Y = "Y";
  public static String PID = "pid";
  public static String BID = "bid";
  public static String TYP = "Typ";
  public static String TEMPERATUR = "Temperatur";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public MeasureRepository(JdbcTemplate jdbcTemplate,
      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public List<EnrichedMeasureDTO> getAll() throws InternalErrorException {
    String sql = "SELECT messdaten.MID, koordinaten.X, koordinaten.Y, messdaten.PID, boeden.Typ, messdaten.Temperatur "
        + "FROM messdaten "
        + "INNER JOIN koordinaten ON messdaten.KID = koordinaten.KID "
        + "INNER JOIN boeden ON messdaten.BID = boeden.BID;";
    try {
      return jdbcTemplate.query(sql, new MeasureRowMapper());
    } catch (EmptyResultDataAccessException e) {
      return Collections.emptyList();
    } catch (DataAccessException e) {
      throw new InternalErrorException();
    }
  }

  public void insertOrUpdate(MeasureRequestDTO measureRequestDTO)
      throws UpdateFailedException {
    String sql = "CALL insert_or_update_messdaten(:pid, :x, :y, :ground, :temperature);";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("pid", measureRequestDTO.pid());
    parameters.addValue("x", measureRequestDTO.x());
    parameters.addValue("y", measureRequestDTO.y());
    parameters.addValue("ground", measureRequestDTO.ground());
    parameters.addValue("temperature", measureRequestDTO.temperature());
    try {
      namedParameterJdbcTemplate.update(sql, parameters);
    } catch (DataAccessException e) {
      throw new UpdateFailedException(e.getMessage());
    }
  }
  public EnrichedMeasureDTO selectByPIDAndXAndY(long pid, int x, int y)
      throws MeasureNotFoundException, InternalErrorException {
    String sql = "SELECT messdaten.MID, koordinaten.X, koordinaten.Y, messdaten.PID, boeden.Typ, messdaten.Temperatur "
        + "FROM messdaten "
        + "INNER JOIN koordinaten ON messdaten.KID = koordinaten.KID "
        + "INNER JOIN boeden ON messdaten.BID = boeden.BID WHERE PID = :pid AND X = :x AND Y = :y;";
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue("pid", pid);
    parameterSource.addValue("x", x);
    parameterSource.addValue("y", y);
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, parameterSource,
          new MeasureRowMapper());
    } catch (EmptyResultDataAccessException e) {
      throw new MeasureNotFoundException("pid: " + pid + " x: " + x + " y: " + y );
    } catch (DataAccessException e) {
      throw new InternalErrorException();
    }
  }

  public EnrichedMeasureDTO selectById(long id) throws InternalErrorException, MeasureNotFoundException {
    String sql = "SELECT messdaten.MID, koordinaten.X, koordinaten.Y, messdaten.PID, boeden.Typ, messdaten.Temperatur "
        + "FROM messdaten "
        + "INNER JOIN koordinaten ON messdaten.KID = koordinaten.KID "
        + "INNER JOIN boeden ON messdaten.BID = boeden.BID WHERE MID = :id;";
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("id", id);
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, parameters,
          new MeasureRowMapper());
    } catch (EmptyResultDataAccessException e) {
      throw new MeasureNotFoundException("id: " + id);
    } catch (DataAccessException e) {
      throw new InternalErrorException();
    }
  }
}
