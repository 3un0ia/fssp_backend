package fssp.term_project.movie.watchlist;

public class WatchlistDto {
    public record Item(
            Long tmdbId,
            String title,
            String posterUrl,
            Double averageRating
    ) {}
}
