package com.mycom.socket.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.socket.auth.config.SecurityConfig;
import com.mycom.socket.auth.controller.AuthController;
import com.mycom.socket.auth.dto.request.RegisterRequest;
import com.mycom.socket.auth.dto.response.RegisterResponse;
import com.mycom.socket.auth.config.JWTProperties;
import com.mycom.socket.auth.jwt.JWTUtil;
import com.mycom.socket.auth.service.AuthService;
import com.mycom.socket.auth.service.MailService;
import com.mycom.socket.auth.service.MemberDetailsService;
import com.mycom.socket.global.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private MailService mailService;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private JWTProperties jwtProperties;

    @MockBean
    private MemberDetailsService memberDetailsService;

    private static final String REGISTER_API_URL = "/api/auth/register";
    private static final String REGISTER_SUCCESS_MESSAGE = "회원가입이 완료되었습니다.";
    private static final String EMAIL_UNVERIFIED_MESSAGE = "이메일 인증이 필요합니다.";

    @Test
    @WithMockUser
    void 회원가입_성공() throws Exception {
        // given
        RegisterRequest request = createRegisterRequest("test@example.com", "testUser", "password123");
        RegisterResponse expectedResponse = createRegisterResponse(1L, "test@example.com", "testUser");
        given(authService.register(any(RegisterRequest.class))).willReturn(expectedResponse);

        // when
        ResultActions resultActions = performRegisterRequest(request);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("testUser"))
                .andExpect(jsonPath("$.message").value(REGISTER_SUCCESS_MESSAGE));
    }

    @Test
    @WithMockUser
    void 회원가입_실패_이메일_미인증() throws Exception {
        // given
        RegisterRequest request = createRegisterRequest("test@example.com", "testUser", "password123");
        given(authService.register(any(RegisterRequest.class)))
                .willThrow(new BadRequestException(EMAIL_UNVERIFIED_MESSAGE));

        // when
        ResultActions resultActions = performRegisterRequest(request);

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(EMAIL_UNVERIFIED_MESSAGE));
    }

    @Test
    @WithMockUser
    void 회원가입_실패_잘못된_입력값() throws Exception {
        // given
        RegisterRequest request = createRegisterRequest("invalid-email", "t", "123");

        // when
        ResultActions resultActions = performRegisterRequest(request);

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    private RegisterRequest createRegisterRequest(String email, String nickname, String password) {
        return new RegisterRequest(email, nickname, password, "안녕하세요");
    }

    private RegisterResponse createRegisterResponse(Long memberId, String email, String nickname) {
        return RegisterResponse.of(memberId, email, nickname);
    }
    private ResultActions performRegisterRequest(RegisterRequest request) throws Exception {
        return mockMvc.perform(post(REGISTER_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
    }
}