package de.heinrichhertzschule.restapidatabase.domain.planet;

import de.heinrichhertzschule.restapidatabase.domain.planet.model.EnrichedPlanetDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetDetailsResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetFieldResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetRequestDTO;
import de.heinrichhertzschule.restapidatabase.domain.planet.model.PlanetResponseDTO;
import de.heinrichhertzschule.restapidatabase.domain.roboter.model.RoboterResponseDTO;
import de.heinrichhertzschule.restapidatabase.error.exceptions.badrequest.BadRequestException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.internalerror.InternalErrorException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.NotFoundException;
import de.heinrichhertzschule.restapidatabase.error.exceptions.notfound.PlanetNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlanetService {
private final static Logger logger = LoggerFactory.getLogger(PlanetService.class);
  private final PlanetRepository planetRepository;

  public PlanetService(PlanetRepository planetRepository) {
    this.planetRepository = planetRepository;
  }

  public PlanetDetailsResponseDTO queryPlanetDetails(long planetId)
      throws InternalErrorException, PlanetNotFoundException {
    return map(planetRepository.getPlanetFields(planetId));
  }

  private PlanetResponseDTO map(PlanetDTO planetDTO) {
    return new PlanetResponseDTO(planetDTO.PID(), planetDTO.Name(), planetDTO.Breite(),
        planetDTO.Hoehe());
  }

  private PlanetDetailsResponseDTO map(List<EnrichedPlanetDTO> enrichedPlanets) {
    EnrichedPlanetDTO firstEnrichedPlanet = enrichedPlanets.get(0);
    long pid = firstEnrichedPlanet.pid();
    String planetName = firstEnrichedPlanet.planetName();
    int width = firstEnrichedPlanet.width();
    int height = firstEnrichedPlanet.height();
    Map<Long, PlanetFieldResponseDTO> coordMessdataMap = new HashMap<>();
    for (EnrichedPlanetDTO enrichedPlanet : enrichedPlanets) {
      if(enrichedPlanet.rid() == null && enrichedPlanet.mid() == null) {
        continue;
      }
      Long kid = enrichedPlanet.kid();
      if (kid != null) {
        PlanetFieldResponseDTO planetField = coordMessdataMap.get(kid);
        if (planetField == null) {
          coordMessdataMap.put(kid,
              new PlanetFieldResponseDTO(enrichedPlanet.x(), enrichedPlanet.y(),
                  enrichedPlanet.ground(), enrichedPlanet.Temperature(), new ArrayList<>()));
        }
        if (enrichedPlanet.rid() != null) {
          coordMessdataMap.get(kid).roboter().add(
              new RoboterResponseDTO(enrichedPlanet.rid(), enrichedPlanet.pid(), enrichedPlanet.x(),
                  enrichedPlanet.y(), enrichedPlanet.roboterName(),
                  enrichedPlanet.robotTemperature(), enrichedPlanet.energy(), enrichedPlanet.direction(),
                  enrichedPlanet.status(), enrichedPlanet.heater(), enrichedPlanet.cooler()));
        }
      }
    }
    return new PlanetDetailsResponseDTO(new PlanetResponseDTO(pid, planetName, width, height),
        coordMessdataMap.values().stream().toList());
  }

  public List<PlanetResponseDTO> getAllPlanets() {
    List<PlanetResponseDTO> result = new ArrayList<>();
    for (PlanetDTO planetDTO : planetRepository.getAll()) {
      result.add(map(planetDTO));
    }
    return result;
  }

  public PlanetResponseDTO getPlanetById(long id)
      throws InternalErrorException, NotFoundException {
    return map(planetRepository.selectById(id));
  }

  public PlanetResponseDTO getPlanet(PlanetRequestDTO planet)
      throws InternalErrorException, NotFoundException {
    return map(planetRepository.selectBy(planet));
  }

  public PlanetResponseDTO insert(PlanetRequestDTO planet)
      throws BadRequestException, InternalErrorException {
    planetRepository.insert(planet.name(), planet.width(), planet.height());
    try {
      return getPlanet(planet);
    } catch (NotFoundException e) {
      logger.error("NotFoundException after insert - planet: " + planet.toString());
      throw new InternalErrorException();
    }
  }
}
