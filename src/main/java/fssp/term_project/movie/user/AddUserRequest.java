package fssp.term_project.movie.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest {
    private String name;
    private String email;
    private String password;
}