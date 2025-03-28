package fssp.term_project.movie.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity @Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    /*
    * 이름, 이메일(아이디), 비밀번호
    * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Builder
    public User(String name, String email, String password, String auth) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override // 사용자 id(고유값) 반환
    public String getUsername() {
        return this.email;
    }
}
