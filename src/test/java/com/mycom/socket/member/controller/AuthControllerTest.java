package com.mycom.socket.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.socket.auth.config.SecurityConfig;
import com.mycom.socket.auth.controller.AuthController;
import com.mycom.socket.auth.dto.request.RegisterRequestDto;
import com.mycom.socket.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
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

    @Test
    @WithMockUser
    void 회원가입_성공() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto(
                "test@example.com",
                "testUser",
                "password123",
                "안녕하세요"
        );
        given(authService.register(any(RegisterRequestDto.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    @WithMockUser
    void 회원가입_실패_잘못된_입력값() throws Exception {
        // given
        RegisterRequestDto request = new RegisterRequestDto(
                "invalid-email",
                "t",
                "123",
                "안녕하세요"
        );

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
