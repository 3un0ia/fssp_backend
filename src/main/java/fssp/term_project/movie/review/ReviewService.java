package fssp.term_project.movie.review;

import fssp.term_project.movie.user.User;
import fssp.term_project.movie.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;

    public ReviewService(ReviewRepository reviewRepo,
                         UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.userRepo   = userRepo;
    }
    @Transactional
    public Review addReview(String userEmail,
                            Long tmdbId,
                            ReviewDto.Request req) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userEmail));

        Review review = new Review().builder()
                        .tmdbId(tmdbId).user(user)
                        .rating(req.rating())
                        .content(req.content())
                        .createdAt(LocalDateTime.now()).build();
        return reviewRepo.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto.Response> listByMovie(Long tmdbId) {
        return reviewRepo.findByTmdbIdOrderByCreatedAtAsc(tmdbId)
                .stream()
                .map(ReviewDto.Response::from)
                .collect(Collectors.toList());
    }
}
