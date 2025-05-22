package fssp.term_project.movie.movie;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "movies",
        uniqueConstraints = @UniqueConstraint(columnNames = "tmdbId"))
@NoArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long tmdbId;

    private Double localRating;

    private Boolean favorite;

    public Movie(Long tmdbId) {
        this.tmdbId = tmdbId;
        this.localRating = null;
        this.favorite = false;
    }

    public Double getLocalRating() { return localRating; }
    public Boolean getFavorite() { return favorite; }
    public void setLocalRating(Double localRating) {
        this.localRating = localRating;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}