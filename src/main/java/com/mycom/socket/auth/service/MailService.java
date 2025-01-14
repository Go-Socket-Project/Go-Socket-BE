package com.mycom.socket.auth.service;

import com.mycom.socket.auth.config.MailProperties;
import com.mycom.socket.auth.dto.response.EmailVerificationResponse;
import com.mycom.socket.global.exception.BaseException;
import com.mycom.socket.global.service.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;
    private final MailProperties mailProperties;

    /**
     * 6자리 인증번호 생성 (100000-999999)
     */
    private String createVerificationCode() {
        // Math.random()은 예측 가능한 난수를 생성할 수 있어 보안에 취약
        // SecureRandom은 암호학적으로 안전한 난수를 생성하므로 인증번호 생성에 더 적합
        return String.format("%06d", new SecureRandom().nextInt(1000000));
    }

    public boolean isEmailVerified(String email) {
        return redisService.isEmailVerified(email);
    }

    /**
     * 인증메일 생성
     * @param email 수신자 이메일 주소
     * @return 생성된 인증메일
     */
    public MimeMessage createMail(String email, String verificationCode) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(mailProperties.getSenderEmail());
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("이메일 인증");
            String body = String.format(mailProperties.getBodyTemplate(), verificationCode);
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            throw new BaseException("이메일 생성 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        return message;
    }

    /**
     * 인증메일 발송 및 인증번호 반환
     * @param email 수신자 이메일 주소
     * @return 생성된 인증번호
     */
    public EmailVerificationResponse sendMail(String email) {
        if (redisService.incrementCount(email) > 3) {
            throw new BaseException("너무 많은 요청입니다. 1분 후에 다시 시도해주세요.",
                    HttpStatus.TOO_MANY_REQUESTS);
        }

        String verificationCode = createVerificationCode();
        redisService.saveCode(email, verificationCode);

        MimeMessage message = createMail(email, verificationCode);
        try {
            javaMailSender.send(message);
            return EmailVerificationResponse.of("이메일 전송 성공");  // 메시지 수정
        } catch (Exception e) {
            throw new BaseException("이메일 발송 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 인증번호 검증
     *
     * @param email 수신자 이메일 주소
     * @param code  사용자가 입력한 인증번호
     * @return 인증번호 일치 여부
     */
    public EmailVerificationResponse verifyCode(String email, String code) {
        if (!code.matches("\\d{6}")) {
            throw new BaseException("유효하지 않은 인증 코드 형식입니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            String saveCode = redisService.getCode(code);  // 인증코드 검증
            if(!saveCode.equals(code)) {
                throw new BaseException("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
            return EmailVerificationResponse.of("이메일 인증이 완료되었습니다.");
        } catch (Exception e) {
            throw new BaseException("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

}

