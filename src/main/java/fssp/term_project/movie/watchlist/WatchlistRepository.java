package fssp.term_project.movie.watchlist;

import fssp.term_project.movie.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    boolean existsByUserAndTmdbId(User user, Long tmdbId);
    List<Watchlist> findByUser(User user);
    Optional<Watchlist> findByUserAndTmdbId(User user, Long tmdbId);
    Optional<Watchlist> findByTmdbId(Long tmdbId);
}