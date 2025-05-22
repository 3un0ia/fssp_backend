package fssp.term_project.movie.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity @Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    /*
    * 이름, 이메일(아이디), 비밀번호
    * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Builder
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_preferred_genre_ids", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "genre_id", nullable = false)
    private Set<Integer> preferredGenreIds = new HashSet<>();

    public void setPreferredGenreIds(Set<Integer> preferredGenreIds) {
        this.preferredGenreIds = preferredGenreIds;
    }
}
