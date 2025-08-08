package fssp.term_project.movie.review;

import fssp.term_project.movie.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews",
        indexes = @Index(name = "idx_reviews_tmdb", columnList = "tmdbId"))
@Getter @RequiredArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tmdbId;             // TMDB 영화 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;               // 작성자

    @Column(nullable = false)
    private Double rating;           // 별점

    @Column(length = 2000)
    private String content;          // 리뷰 텍스트

    @Column(nullable = false)
    private LocalDateTime createdAt; // 작성 시각

    @Builder
    public Review(Long tmdbId, User user, Double rating, String content, LocalDateTime createdAt){
         this.tmdbId = tmdbId;
         this.user = user;
         this.rating = rating;
         this.content = content;
         this.createdAt = createdAt;
    }
}