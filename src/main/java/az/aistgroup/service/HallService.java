package az.aistgroup.service;

import az.aistgroup.domain.dto.HallDto;

import java.util.List;

public interface HallService {
    List<HallDto> getAllHalls();

    HallDto getHallById(Long id);

    HallDto addHall(HallDto hallDto);

    HallDto updateHall(Long id, HallDto hallDto);

    void deleteHall(Long id);
}
