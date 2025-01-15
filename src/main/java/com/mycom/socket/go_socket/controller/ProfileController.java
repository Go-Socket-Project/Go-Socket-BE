package com.mycom.socket.go_socket.controller;

import com.mycom.socket.auth.security.MemberDetails;
import com.mycom.socket.go_socket.dto.request.PasswordUpdateRequest;
import com.mycom.socket.go_socket.dto.request.ProfileUpdateRequest;
import com.mycom.socket.go_socket.dto.response.ProfileResponse;
import com.mycom.socket.go_socket.entity.Member;
import com.mycom.socket.go_socket.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final MemberService memberService;

    @GetMapping
    public ProfileResponse getProfile(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ProfileResponse.of(memberDetails.getMember());
    }

    @PutMapping
    public ProfileResponse updateProfile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody ProfileUpdateRequest request
    ) {
        Member updatedMember = memberService.updateProfile(
                memberDetails.getMember().getEmail(),
                request.nickname(),
                request.intro()
        );
        return ProfileResponse.of(updatedMember);
    }

    @PutMapping("/password")
    public void updatePassword(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody PasswordUpdateRequest request
    ) {
        memberService.updatePassword(
                memberDetails.getMember().getEmail(),
                request.currentPassword(),
                request.newPassword()
        );
    }

}
