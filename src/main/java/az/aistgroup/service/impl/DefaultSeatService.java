package az.aistgroup.service.impl;

import az.aistgroup.domain.dto.SeatDto;
import az.aistgroup.domain.entity.Seat;
import az.aistgroup.exception.ResourceNotFoundException;
import az.aistgroup.repository.SeatRepository;
import az.aistgroup.service.SeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class DefaultSeatService implements SeatService {
    private final SeatRepository seatRepository;

    public DefaultSeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatDto> getAllSeats() {
        return seatRepository.getAllSeats();
    }

    @Override
    @Transactional(readOnly = true)
    public SeatDto getSeatById(Long id) {
        return seatRepository.findById(id)
                .map(seat -> new SeatDto(seat.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Seat with " + id + " not found!"));
    }

    @Override
    @Transactional
    public SeatDto addSeat(SeatDto seatDto) {
        if (seatDto == null) {
            throw new IllegalArgumentException("seatDto can not be null!");
        }

        var seat = new Seat();
        seat.setName(seatDto.name());
        //TODO: Set hall in here
        Seat newSeat = seatRepository.save(seat);

        //TODO: Use mapper
        return new SeatDto(newSeat.getName());
    }

    @Override
    @Transactional
    public SeatDto updateSeat(final Long id, final SeatDto seatDto) {
        Objects.requireNonNull(id, "id can not be null!");
        Objects.requireNonNull(seatDto, "seatDto can not be null!");

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat with " + id + " not found!"));

        seat.setName(seatDto.name());
        //TODO: Set hall in here

        Seat updatedSeat = seatRepository.save(seat);

        return new SeatDto(updatedSeat.getName());
    }

    @Override
    @Transactional
    public void deleteSeat(final Long id) {
        Objects.requireNonNull(id, "id cannot be null!");

        seatRepository.findById(id)
                .ifPresentOrElse(seatRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Seat with " + id + " not found!");
                        });
    }
}
