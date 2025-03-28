package fssp.term_project.movie.user.config;

import fssp.term_project.movie.user.service.UserDetailService;
import fssp.term_project.movie.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailService userDetailService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth // 인증, 인가 설정
                        .requestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin // 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                )
                .logout(logout -> logout.logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
    /*
    * requestMatchers() : 특정 요청과 일치하는 url에 대한 액세스 설정
    * permitAll() : 누구나 접근 가능하게 설정. "/login", "/signup", "/user"로 요청 오면
    *               인증, 인가 없이 접근 가능
    * anyRequest() : 위에서 설정한 url 이외의 요청
    * authenticated() : 별도의 인가는 필요하지 않지만, 인증이 성공된 상태에서 접근 가능
    *
    * loginPage() : 로그인 페이지 경로 설정
    * defaultSuccessUrl() : 로그인이 완료되었을 때 이동할 경로
    * invalidHttpSession() : 로그아웃 후 세션을 전체 삭제할지 여부
    * csrf(AbstractHttpConfigurer::disable) : csrf 설정 비활성화
    * */


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService); // 사용자 정보 서비스 저장
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
