package az.aistgroup.service;

import az.aistgroup.domain.dto.MovieSessionDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MovieSessionService {
    List<MovieSessionDto> getSessions();

    MovieSessionDto getSessionById(Long id);

    MovieSessionDto addSession(MovieSessionDto sessionDto);

    MovieSessionDto updateSession(Long id, MovieSessionDto sessionDto);

    void deleteSession(Long id);
}
