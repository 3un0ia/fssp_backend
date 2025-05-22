package fssp.term_project.movie.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UserDto {

    public record SignupReq (
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotBlank String name,
            Set<@NotBlank Integer> preferredGenreIds
    ) {}

    public record LoginReq (
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record InfoRes(
            Long id,
            String email,
            String name,
            Set<Integer> preferredGenreIds
    ) {}

    public record LoginRes(
            String jwt,
            Long id,
            String email,
            String name,
            Set<Integer> preferredGenreIds
    ) {}

    public record SocialSignupReq(
            Long userId,
            Set<@NotBlank Integer> preferredGenreIds
    ) {}

    public record SocialLoginReq(
            @NotNull SocialProvider provider,
            @NotBlank String token
    ) {}

    public record SocialLoginRes(
            LoginStatus status,
            String jwt,
            Long userId,
            String email,
            String name
    ) {}

    public enum LoginStatus {
        EXISTING_USER, NEW_USER
    }

    public enum SocialProvider {
        KAKAO, GOOGLE
    }
}
