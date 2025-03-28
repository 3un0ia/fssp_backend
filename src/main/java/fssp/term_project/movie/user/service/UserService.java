package fssp.term_project.movie.user.service;

import fssp.term_project.movie.user.dto.AddUserRequest;
import fssp.term_project.movie.user.User;
import fssp.term_project.movie.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build();
        User saved = userRepository.save(user);
        return saved.getId();
    }
    private void logout() {

    }

    private void signup() {

    }


}
