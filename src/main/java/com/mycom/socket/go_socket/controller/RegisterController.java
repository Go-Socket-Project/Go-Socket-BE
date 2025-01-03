package com.mycom.socket.go_socket.controller;

import com.mycom.socket.go_socket.dto.request.MemberRegisterDto;
import com.mycom.socket.go_socket.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public Long register(@Valid @RequestBody MemberRegisterDto request){
        return registerService.register(request);
    }
}
