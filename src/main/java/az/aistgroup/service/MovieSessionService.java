package az.aistgroup.service;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.dto.MovieSessionView;

import java.util.List;

public interface MovieSessionService {
    List<MovieSessionView> getSessions();

    MovieSessionDto getSessionById(Long id);

    MovieSessionDto addSession(MovieSessionDto sessionDto);

    MovieSessionDto updateSession(Long id, MovieSessionDto sessionDto);

    void deleteSession(Long id);
}
