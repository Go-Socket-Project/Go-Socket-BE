package com.mycom.socket.auth.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String secret;
    private long accessTokenValidityInSeconds;
    private long refreshTokenValidityInSeconds;
    private String accessTokenCookieName;
    private String refreshTokenCookieName;
    private String issuer = "go_socket";
    private boolean secureCookie = false;
}
