package az.aistgroup.repository;

import az.aistgroup.domain.dto.MovieSearchCriteria;
import az.aistgroup.domain.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m " +
            "WHERE (:#{#criteria.name} IS NULL OR m.name LIKE %:#{#criteria.name}%) " +
            "AND (:#{#criteria.genre} IS NULL OR m.genre = :#{#criteria.genre}) " +
            "AND (:#{#criteria.imdbRating} IS NULL OR m.imdbRating = :#{#criteria.imdbRating}) " +
            "AND (:#{#criteria.releaseYear} IS NULL OR m.releaseYear = :#{#criteria.releaseYear})")
    List<Movie> findByCriteria(@Param("criteria") MovieSearchCriteria criteria);
}
