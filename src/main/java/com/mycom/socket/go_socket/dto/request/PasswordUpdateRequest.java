package com.mycom.socket.go_socket.dto.request;

public record PasswordUpdateRequest(
        String currentPassword,
        String newPassword
) {}
