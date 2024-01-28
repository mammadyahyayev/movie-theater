package az.aistgroup.repository;

import az.aistgroup.domain.dto.MovieDto;
import az.aistgroup.domain.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
    @Query("select m from Movie m")
    List<MovieDto> findAllForMovieView();

    List<Movie> findByNameIsContainingIgnoreCase(String name);
}
