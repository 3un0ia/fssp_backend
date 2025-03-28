package fssp.term_project.movie.user.controller;

import fssp.term_project.movie.user.dto.AddUserRequest;
import fssp.term_project.movie.user.service.EmailService;
import fssp.term_project.movie.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Controller
public class UserController {
    private final UserService userService;
    private EmailService emailService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login/mailConfirm")
    public String mailConfirm(@RequestBody String email) throws MessagingException, UnsupportedEncodingException {
        String authCode = emailService.sendEmail(email);
        return authCode;
    }


    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}
