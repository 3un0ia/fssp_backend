package fssp.term_project.movie.review;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies/{tmdbId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 특정 영화에 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewDto.Response> addReview(
            @PathVariable Long tmdbId,
            @Valid @RequestBody ReviewDto.Request req,
            @AuthenticationPrincipal UserDetails user
    ) {
        Review saved = reviewService.addReview(
                user.getUsername(), tmdbId, req
        );
        return ResponseEntity.ok(ReviewDto.Response.from(saved));
    }

    // 특정 영화의 리뷰 목록 조회
    @GetMapping
    public ResponseEntity<List<ReviewDto.Response>> listReviews(@PathVariable Long tmdbId) {
        return ResponseEntity.ok(reviewService.listByMovie(tmdbId));
    }
}
