package fssp.term_project.movie.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(60))
                .maximumSize(10_000);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object,Object> caf) {
        CaffeineCacheManager mgr = new CaffeineCacheManager("movies");
        mgr.setCaffeine(caf);
        return mgr;
    }
}
