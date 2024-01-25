package az.aistgroup.domain.entity;

import az.aistgroup.domain.enumeration.MovieGenre;
import jakarta.persistence.*;

@Entity
@Table(name = "movies")
public class Movie extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_generator")
    @SequenceGenerator(name = "movie_generator", sequenceName = "movie_seq", allocationSize = 1)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MovieGenre genre;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }
}
