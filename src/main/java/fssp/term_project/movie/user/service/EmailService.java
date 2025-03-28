package fssp.term_project.movie.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender; // 의존성 주입으로 필요한 객체
    private String authNum; // 랜덤 인증코드

    // 랜덤 인증코드 생성
    public void createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch(index) {
                case 0:
                    key.append((char) ( (int)random.nextInt(26) + 97 ));
                    break;
                case 1:
                    key.append((char) ( (int)random.nextInt(26) + 65 ));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }
        authNum = key.toString();
    }

    // 메일 양식 작성
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {

        createCode();
        String from = "kj122kj@naver.com";

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("[영화추천서비스] 회원가입 인증 번호");
        message.setFrom(from);
        message.setText("안녕하세요, [영화추천서비스] 입니다.\n 아래 코드를 회원가입 창으로 돌아가 입력해주세요. \n\n" +
                authNum + "\n회원가입 인증 코드 입니다.", "utf-8");

        return message;
    }

    // 실제 메일 전송
    public String sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage emailForm = createEmailForm(toEmail);
        emailSender.send(emailForm);
        return authNum; // 인증코드 반환
    }

}
