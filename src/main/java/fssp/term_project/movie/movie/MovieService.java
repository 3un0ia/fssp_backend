package fssp.term_project.movie.movie;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import fssp.term_project.movie.movie.MovieDto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final TmdbMovieClient tmdbClient;

    public MovieService(MovieRepository movieRepository, TmdbMovieClient tmdbMovieClient) {
        this.movieRepository = movieRepository;
        this.tmdbClient = tmdbMovieClient;
    }

    /**
     * TMDB 인기 영화 목록
     */
    public List<SummaryRes> listPopular(int limit) {
        return tmdbClient.fetchPopular(limit);
    }

    /** 2. 일간 트렌딩 영화 (Trending by day) */
    public List<SummaryRes> listTrendingDay(int limit) {
        return tmdbClient.fetchTrending("day", limit);
    }

    /** 3. 주간 트렌딩 영화 (Trending by week) */
    public List<SummaryRes> listTrendingWeek(int limit) {
        return tmdbClient.fetchTrending("week", limit);
    }

    /**
     * 4. 키워드 검색
     */
    public List<SummaryRes> search(String query, int limit) {
        return tmdbClient.search(query, limit);
    }

    /**
     * 5. 선호 장르 기반 추천 (Discover)
     */
    public List<SummaryRes> recommendByGenres(List<Integer> genreIds, int limit) {
        return tmdbClient.discoverByGenres(genreIds, limit);
    }

    /**
     * 5-1. 사용자 선호 장르 기반 추천
     */
    public List<MovieDto.SummaryRes> recommendByUserPreferences(Set<Integer> preferredGenreIds, int limit) {
        if (preferredGenreIds.isEmpty()) {
            return List.of();  // 선호 장르가 없으면 빈 리스트
        }
        return tmdbClient.discoverByGenres(new ArrayList<>(preferredGenreIds), limit);
    }

    /**
     * 5-2. 장르별 인기 영화 추천
     */
    public List<SummaryRes> groupPopularByGenre(Integer genreId, int limit) {

            // Discover에 sort_by=popularity.desc 로 default 되어 있으니 인기 영화
            return tmdbClient.discoverByGenres(List.of(genreId), limit);
    }

    /**
     * 6. 영화 상세정보
     */
    @Cacheable(value = "movies", key = "'summary:' + #tmdbId")
    public DetailRes getDetail(Long tmdbId) {
        return tmdbClient.fetchDetail(tmdbId);
    }

    /** 7. 단일 영화 요약 (제목·포스터·평점) */
    public SummaryRes getSummary(Long tmdbId) {
        return tmdbClient.fetchSummary(tmdbId);
    }

    /** 8. 다중 영화 요약 일괄 조회 */
    public List<SummaryRes> getSummaries(List<Long> tmdbIds) {
        return tmdbClient.fetchSummaries(tmdbIds);
    }

    /**
     * 로컬에 없으면 Movie 엔티티 생성
     */
    @Transactional
    public Movie ensureMovie(Long tmdbId) {
        return movieRepository.findByTmdbId(tmdbId)
                .orElseGet(() -> {
                    Movie m = new Movie(tmdbId);
                    return movieRepository.save(m);
                });
    }

    /**
     * 사용자 로컬 상태(평점) 업데이트
     */
    @Transactional
    public void updateRating(Long tmdbId, Double rating) {
        Movie movie = ensureMovie(tmdbId);
        movie.setLocalRating(rating);
        movieRepository.save(movie);
    }
}
