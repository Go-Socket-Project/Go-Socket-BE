package com.mycom.socket.go_socket.dto.request;

public record ProfileUpdateRequest(
        String nickname,
        String intro
) {}