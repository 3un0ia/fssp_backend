package fssp.term_project.movie.watchlist;

import fssp.term_project.movie.movie.MovieDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }


    // 사용자 즐겨찾기(워치리스트) 목록 조회
    @GetMapping
    public ResponseEntity<List<MovieDto.DetailRes>> listFavorites(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(watchlistService.listFavorites(user.getUsername()));
    }

    // 즐겨찾기 추가
    @PostMapping("/{tmdbId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long tmdbId,
            @AuthenticationPrincipal UserDetails user
    ) {
        System.out.println("user = " + user.toString());
        watchlistService.addFavorite(user.getUsername(), tmdbId);
        return ResponseEntity.ok().build();
    }

    // 즐겨찾기 삭제
    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long tmdbId,
            @AuthenticationPrincipal UserDetails user
    ) {
        watchlistService.removeFavorite(user.getUsername(), tmdbId);
        return ResponseEntity.noContent().build();
    }
}