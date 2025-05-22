package fssp.term_project.movie.watchlist;

import fssp.term_project.movie.user.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "watchlist", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tmdbId"}))
@NoArgsConstructor
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tmdbId;            // TMDB 영화 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;              // 찜한 사용자

    public Long getTmdbId() { return tmdbId;}
    public void setUser(User user) {this.user = user;}
    public void setTmdbId (Long tmdbId) {this.tmdbId = tmdbId;}
}
