package com.mycom.socket.auth.service;

import com.mycom.socket.auth.service.data.VerificationData;
import com.mycom.socket.global.exception.BaseException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RateLimiter rateLimiter; // 인증 번호 요청 제한

    private final Map<String, VerificationData> verificationDataMap = new ConcurrentHashMap<>();
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * 6자리 난수 인증번호 생성
     * SecureRandom 사용하여 보안성 향상
     * @return 100000~999999 범위의 인증번호
     */
    private String createVerificationCode() {
        // Math.random()은 예측 가능한 난수를 생성할 수 있어 보안에 취약
        // SecureRandom은 암호학적으로 안전한 난수를 생성하므로 인증번호 생성에 더 적합
        SecureRandom secureRandom = new SecureRandom();
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

    /**
     * 인증메일 생성
     * @param mail 수신자 이메일 주소
     * @return 생성된 인증메일
     */
    public MimeMessage createMail(String mail, String verificationCode) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = String.format("""
                   <h3>요청하신 인증 번호입니다.</h3>
                   <h1>%s</h1>
                   <h3>감사합니다.</h3>
                   """, verificationCode);
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            throw new BaseException("이메일 생성 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        return message;
    }

    /**
     * 이메일 유효성 검사
     *
     * @param email 검사할 이메일 주소
     * @return 유효한 이메일 주소인지 여부
     */
    private boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * 인증메일 발송 및 인증번호 반환
     * @param mail 수신자 이메일 주소
     * @return 생성된 인증번호
     */
    public boolean sendMail(String mail) {
        if (!isValidEmail(mail)) {
            throw new BaseException("유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST);
        }

        rateLimiter.checkRateLimit(mail);
        String verificationCode = createVerificationCode();
        verificationDataMap.put(mail, new VerificationData(verificationCode));

        MimeMessage message = createMail(mail, verificationCode);
        try{
            javaMailSender.send(message);
            return true;
        }catch (Exception e) {
            throw new BaseException("이메일 발송 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 인증번호 검증
     * @param email 수신자 이메일 주소
     * @param code 사용자가 입력한 인증번호
     * @return 인증번호 일치 여부
     */
    public boolean verifyCode(String email, String code) {
        if (!isValidEmail(email)){
            throw new BaseException("유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(code) || !code.matches("\\d{6}")) {
            return false;
        }

        VerificationData data = verificationDataMap.get(email);

        if (data == null || data.isExpired()) {
            return false;
        }

        boolean isVerified = data.code().equals(code);

        if (isVerified){
            verificationDataMap.remove(email);
        }

        return isVerified;
    }
}

