package fssp.term_project.movie.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ReviewDto {
    /** 리뷰 작성 요청 */
    public record Request(
            @NotNull @Min(0) @Max(10) Double rating,
            @NotBlank String content
    ) {}

    /** 리뷰 응답 (단일) */
    public record Response(
            Long id,
            Long tmdbId,
            String userName,
            Double rating,
            String content,
            LocalDateTime createdAt
    ) {
        public static Response from(Review r) {
            return new Response(
                    r.getId(),
                    r.getTmdbId(),
                    r.getUser().getName(),
                    r.getRating(),
                    r.getContent(),
                    r.getCreatedAt()
            );
        }
    }
}