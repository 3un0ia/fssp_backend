package fssp.term_project.movie.movie;

import fssp.term_project.movie.movie.MovieDto.*;
import fssp.term_project.movie.user.User;
import fssp.term_project.movie.user.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;
    private final UserRepository userRepository;

    public MovieController(UserRepository userRepository, MovieService service) {
        this.movieService = service;
        this.userRepository = userRepository;
    }

    // 영화 상세정보 보기
    @GetMapping("/{tmdbId}")
    public ResponseEntity<MovieDto.DetailRes> detail(@PathVariable Long tmdbId) {
        return ResponseEntity.ok(movieService.getDetail(tmdbId));
    }

    // 검색화면 - 영화 검색하기
    @GetMapping("/search")
    public ResponseEntity<List<MovieDto.SummaryRes>> search(@RequestParam String q, @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(movieService.search(q, limit));
    }

    // 메인화면 - 인기 영화 리스트
    @GetMapping
    public ResponseEntity<List<SummaryRes>> listPopular(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(movieService.listPopular(limit));
    }
    // 추천 (선택 선호 장르 기반 종합 추천)
    @GetMapping("/recommend/combined")
    public ResponseEntity<List<SummaryRes>> recommendCombined(@AuthenticationPrincipal UserDetails user,
                                                              @RequestParam(defaultValue="10") int limit) {
               User u = userRepository.findByEmail(user.getUsername()).orElseThrow();
               return ResponseEntity.ok( movieService.recommendByUserPreferences(u.getPreferredGenreIds(), limit));
    }
    // 메인화면 - 장르별 추천 영화 리스트
    @GetMapping("/recommend/genres")
    public ResponseEntity<Map<String,List<SummaryRes>>> recommendByGenres( @AuthenticationPrincipal UserDetails user,
                                                                           @RequestParam(defaultValue = "5")int limit) {
        User u = userRepository.findByEmail(user.getUsername()).orElseThrow();
        return ResponseEntity.ok(movieService.groupPopularByGenre(u.getPreferredGenreIds(), limit));
    }

    // 평점 저장
    @PostMapping("/{tmdbId}/rating")
    public ResponseEntity<Void> updateRating(@PathVariable Long tmdbId, @RequestBody LocalUpdateReq req ) {
        movieService.updateRating(tmdbId, req.rating());
        return ResponseEntity.ok().build();
    }

    public static record LocalUpdateReq(
            @NotNull Double rating,
            @NotNull Boolean favorite) {}
}
