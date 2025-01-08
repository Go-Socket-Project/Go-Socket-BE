package com.mycom.socket.auth.service;

import com.mycom.socket.global.exception.BaseException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final Map<String, Integer> verificationCodes = new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String senderEmail;


    // 랜덤으로 숫자 생성
    private int createVerificationCode() {
        // Math.random()은 예측 가능한 난수를 생성할 수 있어 보안에 취약
        // SecureRandom은 암호학적으로 안전한 난수를 생성하므로 인증번호 생성에 더 적합
        SecureRandom secureRandom = new SecureRandom();
        return 100000 + secureRandom.nextInt(900000);
    }

    public MimeMessage createMail(String mail) {
        int verificationCode = createVerificationCode();
        verificationCodes.put(mail, verificationCode);

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = String.format("""
                <h3>요청하신 인증 번호입니다.</h3>
                <h1>%d</h1>
                <h3>감사합니다.</h3>
                """, verificationCode);
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            throw new BaseException("이메일 생성 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
        return message;
    }

    public int sendMail(String mail) {
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);
        return verificationCodes.get(mail);
    }

    public boolean verifyCode(String email, String code) {
        Integer savedCode = verificationCodes.get(email);
        return savedCode != null && String.valueOf(savedCode).equals(code);
    }
}

