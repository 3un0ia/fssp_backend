package fssp.term_project.movie.user;

import fssp.term_project.movie.user.UserDto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class SocialAuthController {

    private final SocialAuthService socialService;

    public SocialAuthController(SocialAuthService service) {
        this.socialService = service;
    }

    @PostMapping("/login")
    public ResponseEntity<SocialLoginRes> socialLogin(@Valid @RequestBody SocialLoginReq req) {
        SocialLoginRes res = socialService.login(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> socialSignup(@Valid @RequestBody SocialSignupReq req) {
        String jwt = socialService.signup(req);
        return ResponseEntity.ok(Map.of("token", jwt));
    }

}