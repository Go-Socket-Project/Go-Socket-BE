package com.mycom.socket.go_socket.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendStatus {

    PENDING("PENDING", "대기중"),
    ACCEPTED("ACCEPTED", "수락됨"),
    BLOCKED("BLOCKED", "차단됨");

    private final String status;
    private final String description;

    public boolean isAccepted() {
        return this == ACCEPTED;
    }
}
