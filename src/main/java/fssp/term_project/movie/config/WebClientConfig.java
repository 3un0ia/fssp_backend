package fssp.term_project.movie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public WebClient tmdbWebClient(WebClient.Builder builder,
                                   @Value("${tmdb.base-url}") String baseUrl ) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }
}
