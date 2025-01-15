package com.mycom.socket.go_socket.controller;

import com.mycom.socket.auth.security.MemberDetails;
import com.mycom.socket.go_socket.dto.response.ProfileResponse;
import com.mycom.socket.go_socket.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final MemberService memberService;

    @GetMapping
    public ProfileResponse getProfile(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ProfileResponse.of(memberDetails.getMember());
    }
}
