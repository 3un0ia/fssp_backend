package fssp.term_project.movie.movie;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import fssp.term_project.movie.movie.MovieDto.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class TmdbMovieClient {
    private final WebClient webClient;
    private final String apiKey;

    public TmdbMovieClient(WebClient webClient,
                           @Value("${tmdb.api-key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    // TMDB 전체 장르 ID ↔ 이름 맵 조회 (/genre/movie/list)
    //사용처 - UI(장르 선택), RecommendationServiced
    public Map<Integer, String> fetchAllGenres() {
        JsonNode root = webClient.get()
                .uri(uri -> uri
                        .path("/genre/movie/list")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        Map<Integer, String> map = new HashMap<>();
        for (JsonNode g : root.get("genres")) {
            map.put(g.get("id").asInt(), g.get("name").asText());
        }
        return map;
    }

    // 키워드 검색 (/search/movie)
    // 사용처 - MovieService.search
    public List<SummaryRes> search(String query, int limit) {
        JsonNode root = webClient.get()
                .uri(uri -> uri
                        .path("/search/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("query", query)
                        .queryParam("page", 1)
                        .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return StreamSupport.stream(root.get("results").spliterator(), false)
                .limit(limit)
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // 인기 영화 목록 (/movie/popular)
    // 사용처 - MovieService.listPopular
    public List<SummaryRes> fetchPopular(int limit) {
        JsonNode root = webClient.get()
                .uri(uri -> uri
                        .path("/movie/popular")
                        .queryParam("api_key", apiKey)
                        .queryParam("page", 1)
                        .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return StreamSupport.stream(root.get("results").spliterator(), false)
                .limit(limit)
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // 트렌딩 영화 (/trending/movie/{day|week})
    // 사용처 -
    public List<SummaryRes> fetchTrending(String timeWindow, int limit) {
        JsonNode root = webClient.get()
                .uri(uri -> uri
                        .path("/trending/movie/{time_window}")
                        .queryParam("api_key", apiKey)
                        .build(timeWindow)
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return StreamSupport.stream(root.get("results").spliterator(), false)
                .limit(limit)
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // 선호 장르 필터링 (/discover/movie?with_genres=…)
    // 사용처 - MovieService.recommendByGenres
    public List<SummaryRes> discoverByGenres(List<Integer> genreIds, int limit) {
        String withGenres = genreIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        JsonNode root = webClient.get()
                .uri(uri -> uri
                        .path("/discover/movie")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("sort_by", "popularity.desc")
                        .queryParam("with_genres", withGenres)
                        .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return StreamSupport.stream(root.get("results").spliterator(), false)
                .limit(limit)
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // 영화 상세 정보 + 출연진 제작진  (/movie/{id} + /movie/{id}/credits)
    // 사용처 - MovieService.getDetail
    public DetailRes fetchDetail(Long tmdbId) {
        JsonNode m = webClient.get()
                .uri(uri -> uri
                        .path("/movie/{id}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .build(tmdbId)
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        // 장르 이름 리스트
        Set<String> genres = StreamSupport.stream(m.get("genres").spliterator(), false)
                .map(n -> n.get("name").asText())
                .collect(Collectors.toSet());

        // 기본 상세 정보 수집
        DetailRes base = new DetailRes(
                m.get("id").asLong(),
                m.get("title").asText(),
                m.hasNonNull("overview") ? m.get("overview").asText() : "",
                m.hasNonNull("release_date") && !m.get("release_date").asText().isEmpty()
                        ? LocalDate.parse(m.get("release_date").asText())
                        : null,
                genres,
                Collections.emptySet(),
                Collections.emptySet(),
                m.hasNonNull("vote_average") ? m.get("vote_average").asDouble() : 0.0
        );

        // 출연진 & 제작진 조회
        JsonNode credits = webClient.get()
                .uri(uri -> uri
                        .path("/movie/{id}/credits")
                        .queryParam("api_key", apiKey)
                        .build(tmdbId)
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        Set<String> cast = StreamSupport.stream(credits.get("cast").spliterator(), false)
                .map(n -> n.get("name").asText())
                .limit(10)  // 상위 10명만
                .collect(Collectors.toSet());

        Set<String> crew = StreamSupport.stream(credits.get("crew").spliterator(), false)
                .filter(n -> "Director".equals(n.get("job").asText()))
                .map(n -> n.get("name").asText())
                .collect(Collectors.toSet());

        return new DetailRes(
                base.id(), base.title(), base.overview(),
                base.releaseDate(), base.genres(),
                cast, crew, base.rating()
        );
    }

    // 단일 영화 요약 (제목 포스터 평점)
    // 사용처 - WatchlistService, ReviewController (리뷰에 포스터)
    public SummaryRes fetchSummary(Long tmdbId) {
        JsonNode m = webClient.get()
                .uri(uri -> uri
                        .path("/movie/{id}")
                        .queryParam("api_key", apiKey)
                        .build(tmdbId)
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return toSummary(m);
    }

    // 다중 영화 요약 일괄 조회 (내부 fetchSummary 반복)
    // 사용처 - WatchlistService.listFavorites
    public List<SummaryRes> fetchSummaries(List<Long> tmdbIds) {
        return tmdbIds.stream()
                .map(this::fetchSummary)
                .collect(Collectors.toList());
    }

    /** JsonNode → SummaryRes 변환 헬퍼 */
    private SummaryRes toSummary(JsonNode item) {
        String poster = item.hasNonNull("poster_path")
                ? "https://image.tmdb.org/t/p/w500" + item.get("poster_path").asText()
                : null;

        double rating = item.hasNonNull("vote_average")
                ? item.get("vote_average").asDouble()
                : 0.0;

        return new SummaryRes(
                item.get("id").asLong(),
                item.get("title").asText(),
                poster,
                rating
        );
    }
}
