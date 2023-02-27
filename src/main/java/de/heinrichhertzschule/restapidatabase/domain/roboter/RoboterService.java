package de.heinrichhertzschule.restapidatabase.domain.roboter;

import de.heinrichhertzschule.restapidatabase.domain.roboter.model.EnrichedRoboterDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterInsertRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.BadRequestException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.NotFoundException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.RoboterNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoboterService {

  private final RoboterRepository roboterRepository;

  public RoboterService(RoboterRepository roboterRepository) {
    this.roboterRepository = roboterRepository;
  }

  public RoboterResponseDTO insert(RoboterInsertRequestDTO requestDTO)
      throws InternalErrorException, BadRequestException {
    roboterRepository.insert(requestDTO.pid(), requestDTO.name());
    try {
      return getByPIDAndName(requestDTO.pid(), requestDTO.name());
    } catch (NotFoundException ex) {
      throw new InternalErrorException();
    }
  }

  public RoboterResponseDTO getById(long id)
      throws NotFoundException, InternalErrorException {

    return map(roboterRepository.getById(id));
  }

  public RoboterResponseDTO update(long id, RoboterRequestDTO input)
      throws InternalErrorException, BadRequestException {
    roboterRepository.update(id, input);
    try {
      return getById(id);
    } catch (NotFoundException e) {
      throw new InternalErrorException();
    }
  }

  public List<RoboterResponseDTO> getAll() throws InternalErrorException {
    List<RoboterResponseDTO> result = new ArrayList<>();
    for (EnrichedRoboterDTO roboter : roboterRepository.getAll()) {
      result.add(map(roboter));
    }
    return result;
  }

  public RoboterResponseDTO getByPIDAndName(long pid, String name)
      throws RoboterNotFoundException, InternalErrorException {
    return map(roboterRepository.getByPlanetIdAndName(pid, name));
  }

  public RoboterResponseDTO map(EnrichedRoboterDTO input) {
    return new RoboterResponseDTO(input.rid(), input.pid(), input.x(), input.y(), input.name(),
        input.robotTemperature(), input.energy(),
        input.direction(), input.status(), input.heater(), input.cooler());
  }
}
