package az.aistgroup.service;


import az.aistgroup.domain.dto.SeatDto;

import java.util.List;

public interface SeatService {
    List<SeatDto> getAllSeats();

    SeatDto getSeatById(Long id);

    SeatDto addSeat(SeatDto seatDto);

    SeatDto updateSeat(Long id, SeatDto seatDto);

    void deleteSeat(Long id);
}
