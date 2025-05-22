package fssp.term_project.movie.user;

import jakarta.persistence.*;
import fssp.term_project.movie.user.UserDto.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor
@Table(name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "providerId"}))
public class SocialAccount {
    @Id @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SocialProvider provider;

    @Column(nullable = false)
    String providerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Builder
    public SocialAccount(SocialProvider provider, String pid, User user) {
        this.provider = provider;
        this.providerId = pid;
        this.user = user;
    }
}
