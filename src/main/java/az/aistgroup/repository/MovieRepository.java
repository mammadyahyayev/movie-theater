package az.aistgroup.repository;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.entity.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<MovieDto> getAllMovies();

    List<MovieDto> findByNameIsContainingIgnoreCase(String name);
}
