package com.mycom.socket.go_socket.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageStatus {
    SENT("SENT", "전송됨"),
    DELIVERED("DELIVERED", "전달됨"),
    READ("READ", "읽음");

    private final String status;
    private final String description;

    public boolean isRead() {
        return this == READ;
    }
}
