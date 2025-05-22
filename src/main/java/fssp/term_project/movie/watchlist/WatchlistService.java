package fssp.term_project.movie.watchlist;

import fssp.term_project.movie.movie.MovieDto.*;
import fssp.term_project.movie.movie.TmdbMovieClient;
import fssp.term_project.movie.user.User;
import fssp.term_project.movie.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final TmdbMovieClient tmdbMovieClient;

    public WatchlistService(WatchlistRepository watchlistRepository,
                            UserRepository userRepository,
                            TmdbMovieClient tmdbMovieClient) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository  = userRepository;
        this.tmdbMovieClient = tmdbMovieClient;
    }
    /**
     * ① 사용자 이메일로 User 조회
     * ② 해당 사용자의 WatchlistItem 목록 조회
     * ③ 각 tmdbId로 TMDB 요약 정보 일괄 조회
     */
    @Transactional(readOnly = true)
    public List<SummaryRes> listFavorites(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userEmail));

        List<Long> tmdbIds = watchlistRepository.findByUser(user).stream()
                .map(Watchlist::getTmdbId)
                .toList();

        if (tmdbIds.isEmpty()) {
            return List.of();
        }
        return tmdbMovieClient.fetchSummaries(tmdbIds);
    }


    @Transactional
    public void addFavorite(String userEmail, Long tmdbId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userEmail));

        boolean exists = watchlistRepository.existsByUserAndTmdbId(user, tmdbId);
        if (!exists) {
            Watchlist item = new Watchlist();
            item.setUser(user);
            item.setTmdbId(tmdbId);
            watchlistRepository.save(item);
        }
    }

    @Transactional
    public void removeFavorite(String userEmail, Long tmdbId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userEmail));

        watchlistRepository.findByUserAndTmdbId(user, tmdbId)
                .ifPresent(watchlistRepository::delete);
    }
}