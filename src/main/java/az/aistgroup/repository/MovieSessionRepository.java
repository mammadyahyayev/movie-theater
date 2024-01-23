package az.aistgroup.repository;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.MovieSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieSessionRepository extends CrudRepository<MovieSession, Long> {
    List<MovieSessionDto> getAllMovieSessions();
}
