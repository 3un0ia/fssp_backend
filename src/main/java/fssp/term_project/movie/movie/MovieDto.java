package fssp.term_project.movie.movie;

import java.time.LocalDate;
import java.util.Set;

public class MovieDto {

    /** 간략 정보 (추천·리스트) */
    public record SummaryRes(
            Long id,
            String title,
            String posterUrl,
            Double rating
    ) {}

    /** 상세 정보 */
    public record DetailRes(
            Long id,
            String title,
            String overview,
            LocalDate releaseDate,
            Set<String> genres,
            Set<String> cast, //actors
            Set<String> crew, //directors
            Double rating
    ) {}
}
