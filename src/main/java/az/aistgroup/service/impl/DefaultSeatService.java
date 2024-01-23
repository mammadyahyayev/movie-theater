package az.aistgroup.service.impl;

import az.aistgroup.repository.SeatRepository;
import az.aistgroup.service.SeatService;

public class DefaultSeatService implements SeatService {
    private final SeatRepository seatRepository;

    public DefaultSeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }
}
