package az.aistgroup.repository;

import az.aistgroup.domain.dto.MovieSessionViewDto;
import az.aistgroup.domain.entity.MovieSession;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieSessionRepository extends CrudRepository<MovieSession, Long> {
    @Query("""
            select ms.date as date, ms.sessionTime as sessionTime, ms.price as price,
            ms.ticketsLeft as ticketsLeft, m.name as movieName, h.name as hall\s
            from MovieSession ms
                left join ms.movie m
                left join ms.hall h\s
            """)
    List<MovieSessionViewDto> getAllMovieSessions();

    @Query("select ms from MovieSession ms where ms.hall.id = :hallId and ms.date = :date")
    Optional<MovieSession> findActiveSessionForHall(Long hallId, LocalDateTime date);
}
