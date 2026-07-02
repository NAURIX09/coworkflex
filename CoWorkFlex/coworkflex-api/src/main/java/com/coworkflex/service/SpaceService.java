package com.coworkflex.service;

import com.coworkflex.dto.Dtos.DeskDTO;
import com.coworkflex.dto.Dtos.SpaceDTO;
import com.coworkflex.entity.Desk;
import com.coworkflex.entity.DeskType;
import com.coworkflex.entity.Space;
import com.coworkflex.exception.NotFoundException;
import com.coworkflex.repository.DeskRepository;
import com.coworkflex.repository.SpaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final DeskRepository deskRepository;

    public SpaceService(SpaceRepository spaceRepository, DeskRepository deskRepository) {
        this.spaceRepository = spaceRepository;
        this.deskRepository = deskRepository;
    }

    public List<SpaceDTO> findAll(String city, Integer minCapacity) {
        return spaceRepository.findWithFilters(city, minCapacity)
            .stream().map(this::toDTO).toList();
    }

    public SpaceDTO findById(Long id) {
        Space space = spaceRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Espace introuvable : " + id));
        return toDTO(space);
    }

    public List<DeskDTO> findDesks(Long spaceId, DeskType type,
                                   LocalDateTime start, LocalDateTime end) {
        if (!spaceRepository.existsById(spaceId)) {
            throw new NotFoundException("Espace introuvable : " + spaceId);
        }

        List<Desk> desks;
        if (start != null && end != null) {
            desks = (type != null)
                ? deskRepository.findAvailableDesksByType(spaceId, type, start, end)
                : deskRepository.findAvailableDesks(spaceId, start, end);
        } else {
            desks = (type != null)
                ? deskRepository.findBySpaceIdAndType(spaceId, type)
                : deskRepository.findBySpaceId(spaceId);
        }
        return desks.stream().map(this::toDeskDTO).toList();
    }

    public List<String> findAllCities() {
        return spaceRepository.findAllCities();
    }

    private SpaceDTO toDTO(Space s) {
        return new SpaceDTO(
            s.getId(), s.getName(), s.getCity(), s.getAddress(), s.getCapacity(),
            s.getDescription(), s.getImageUrl(), s.getRating(),
            s.getDesks() != null ? s.getDesks().size() : 0
        );
    }

    public DeskDTO toDeskDTO(Desk d) {
        return new DeskDTO(
            d.getId(), d.getLabel(), d.getType(), d.getPricePerHour(), d.getAmenities(),
            d.getAvailable(),
            d.getSpace() != null ? d.getSpace().getId() : null,
            d.getSpace() != null ? d.getSpace().getName() : null
        );
    }
}
