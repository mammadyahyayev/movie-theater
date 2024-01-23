package az.aistgroup.service.impl;

import az.aistgroup.repository.HallRepository;
import az.aistgroup.service.HallService;

public class DefaultHallService implements HallService {
    private final HallRepository hallRepository;

    public DefaultHallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }
}
