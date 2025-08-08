package fssp.term_project.movie.user;

import fssp.term_project.movie.user.UserDto.*;
import com.fasterxml.jackson.databind.JsonNode;
import fssp.term_project.movie.config.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static fssp.term_project.movie.user.UserDto.LoginStatus.EXISTING_USER;
import static fssp.term_project.movie.user.UserDto.LoginStatus.NEW_USER;

@Service
public class SocialAuthService {
    private final SocialAccountRepository socialRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final WebClient webClient;

    public SocialAuthService(SocialAccountRepository socialRepo,
                             UserRepository userRepo,
                             JwtTokenProvider jwtProvider,
                             WebClient webClient) {
        this.socialRepository = socialRepo;
        this.userRepository = userRepo;
        this.jwtProvider = jwtProvider;
        this.webClient = webClient;
    }

    public UserDto.SocialLoginRes login(UserDto.SocialLoginReq req) {
        // 1) 프로필 조회
        SocialProfile profile = switch(req.provider()) {
            case KAKAO -> fetchKakaoProfile(req.token());
            case GOOGLE -> fetchGoogleProfile(req.token());
        };
        // 2) 연동된 소셜 계정 확인
        return socialRepository
                .findByProviderAndProviderId(req.provider(),  profile.id())
                .map(socialAccount -> {
                    User user = socialAccount.getUser();
                    String jwt = jwtProvider.createToken(user.getEmail());
                    return new SocialLoginRes(EXISTING_USER,
                                            jwt, user.getId(), user.getEmail(), user.getName());
                })
                .orElseGet(()-> {
                    User user = User.builder()
                            .email(profile.email)
                            .password(UUID.randomUUID().toString())
                            .name(profile.name)
                            .build();
                    User saved = userRepository.save(user);

                    SocialAccount sa = SocialAccount.builder()
                            .provider(req.provider())
                            .pid(profile.id())
                            .user(saved)
                            .build();
                    SocialAccount savedSa= socialRepository.save(sa);

                    return new SocialLoginRes(NEW_USER, null, saved.getId(), saved.getEmail(), saved.getName());
                });
    }

    public String signup(UserDto.SocialSignupReq req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        user.setPreferredGenreIds(req.preferredGenreIds());
        userRepository.save(user);
        return jwtProvider.createToken(user.getEmail());
    }

    private SocialProfile fetchKakaoProfile(String accessToken) {
        JsonNode node = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        return new SocialProfile(
                node.get("id").asText(),
                node.get("kakao_account").get("email").asText(),
                node.get("kakao_account").get("profile").get("nickname").asText()
        );
    }
    private SocialProfile fetchGoogleProfile(String idToken) {
        JsonNode node = webClient.get()
                .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        return new SocialProfile(
                node.get("sub").asText(),
                node.get("email").asText(),
                node.get("name").asText()
        );
    }

    private record SocialProfile(String id, String email, String name) {}
}