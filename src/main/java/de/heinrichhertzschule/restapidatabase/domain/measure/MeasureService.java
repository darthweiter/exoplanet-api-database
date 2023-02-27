package de.heinrichhertzschule.restapidatabase.domain.measure;

import de.heinrichhertzschule.restapidatabase.domain.measure.model.EnrichedMeasureDTO;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.measure.model.MeasureResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.BadRequestException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.MeasureNotFoundException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MeasureService {

  private final MeasureRepository measureRepository;


  public MeasureService(
      MeasureRepository measureRepository) {
    this.measureRepository = measureRepository;
  }

  public MeasureResponseDTO save(MeasureRequestDTO measure)
      throws InternalErrorException, BadRequestException {
    measureRepository.insertOrUpdate(measure);
    try {
      return selectByPlanetIdAndCoordinateId(measure.pid(), measure.x(), measure.y());
    } catch (NotFoundException e) {
      throw new InternalErrorException();
    }
  }

  public List<MeasureResponseDTO> getAll() throws InternalErrorException {
    List<MeasureResponseDTO> result = new ArrayList<>();
    for (EnrichedMeasureDTO measure : measureRepository.getAll()) {
      result.add(map(measure));
    }
    return result;
  }

  public MeasureResponseDTO selectById(long id)
      throws InternalErrorException, MeasureNotFoundException {
    return map(measureRepository.selectById(id));
  }

  public MeasureResponseDTO selectByPlanetIdAndCoordinateId(long pid, int x, int y)
      throws NotFoundException, InternalErrorException {
    return map(measureRepository.selectByPIDAndXAndY(pid, x, y));
  }

  private MeasureResponseDTO map(EnrichedMeasureDTO input) {
    return new MeasureResponseDTO(input.mid(), input.x(), input.y(), input.pid(),
        input.typ(), input.temperature());
  }
}
