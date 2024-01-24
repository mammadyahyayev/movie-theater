package az.aistgroup.repository;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.MovieSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieSessionRepository extends CrudRepository<MovieSession, Long> {
    List<MovieSessionDto> getAllMovieSessions();

    Optional<MovieSession> findByHallId(Long hallId);
}
