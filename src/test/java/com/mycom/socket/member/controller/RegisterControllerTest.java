package com.mycom.socket.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycom.socket.auth.config.SecurityConfig;
import com.mycom.socket.go_socket.controller.RegisterController;
import com.mycom.socket.go_socket.dto.request.MemberRegisterDto;
import com.mycom.socket.go_socket.service.RegisterService;
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

@WebMvcTest(RegisterController.class)
@Import(SecurityConfig.class)
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterService registerService;

    @Test
    @WithMockUser
    void 회원가입_성공() throws Exception {
        // given
        MemberRegisterDto request = new MemberRegisterDto(
                "test@example.com",
                "testUser",
                "password123",
                "안녕하세요"
        );
        given(registerService.register(any(MemberRegisterDto.class)))
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
        MemberRegisterDto request = new MemberRegisterDto(
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
