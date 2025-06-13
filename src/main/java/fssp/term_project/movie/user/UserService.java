package fssp.term_project.movie.user;

import fssp.term_project.movie.config.JwtTokenProvider;
import fssp.term_project.movie.user.UserDto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository repo, PasswordEncoder encoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = repo;
        this.encoder = encoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 기본 시스템 내 회원가입
    @Transactional
    public InfoRes signup(SignupReq req) {
        String encodedPassword = encoder.encode(req.password());
        User user = User.builder()
                .email(req.email())
                .password(encodedPassword)
                .name(req.name()).build();
        user.setPreferredGenreIds(req.preferredGenreIds());
        User saved = userRepository.save(user);
        return new InfoRes(saved.getId(), saved.getEmail(), saved.getName(), saved.getPreferredGenreIds());
    }
    // 기본 시스템 내 아이디로 로그인
    @Transactional(readOnly = true)
    public LoginRes login(LoginReq req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if(!encoder.matches(req.password(), user.getPassword())) throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        String token = jwtTokenProvider.createToken(user.getEmail());

        return new LoginRes(token, user.getId(), user.getEmail(), user.getName(), user.getPreferredGenreIds());
    }
}
