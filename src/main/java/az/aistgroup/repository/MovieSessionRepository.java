package az.aistgroup.repository;

import az.aistgroup.domain.dto.MovieSessionDto;
import az.aistgroup.domain.entity.MovieSession;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieSessionRepository extends CrudRepository<MovieSession, Long> {
    List<MovieSessionDto> getAllMovieSessions();

    @Query("select ms from MovieSession ms where ms.hall.id = :hallId and ms.date = :date")
    Optional<MovieSession> findActiveSessionForHall(Long hallId, LocalDateTime date);
}
