package az.aistgroup.repository;

import az.aistgroup.domain.entity.MovieSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieSessionRepository extends CrudRepository<MovieSession, Long> {
}
