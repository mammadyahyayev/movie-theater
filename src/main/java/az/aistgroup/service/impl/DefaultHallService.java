package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.HallDto;
import az.aistgroup.domain.entity.Hall;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.HallRepository;
import az.aistgroup.service.HallService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class DefaultHallService implements HallService {
    private final HallRepository hallRepository;

    public DefaultHallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallDto> getAllHalls() {
        return hallRepository.getAllHalls();
    }

    @Override
    @Transactional(readOnly = true)
    public HallDto getHallById(Long id) {
        return hallRepository.findById(id)
                .map(hall -> new HallDto(hall.getName(), hall.getCapacity()))
                .orElseThrow(() -> new ResourceNotFoundException("Hall with " + id + " not found!"));
    }

    @Override
    @Transactional
    public HallDto addHall(HallDto hallDto) {
        if (hallDto == null) {
            throw new IllegalArgumentException("hallDto can not be null!");
        }

        var hall = new Hall();
        hall.setName(hallDto.name());
        hall.setCapacity(hallDto.capacity());
        Hall newHall = hallRepository.save(hall);

        //TODO: Use mapper
        return new HallDto(newHall.getName(), newHall.getCapacity());
    }

    @Override
    @Transactional
    public HallDto updateHall(final Long id, final HallDto hallDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(hallDto, "hallDto can not be null!");

        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hall with " + id + " not found!"));

        hall.setName(hallDto.name());
        hall.setCapacity(hallDto.capacity());

        Hall updatedHall = hallRepository.save(hall);

        return new HallDto(updatedHall.getName(), updatedHall.getCapacity());
    }

    @Override
    @Transactional
    public void deleteHall(final Long id) {
        Objects.requireNonNull(id, "id cannot be null!");

        hallRepository.findById(id)
                .ifPresentOrElse(hallRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Hall with " + id + " not found!");
                        });
    }
}
