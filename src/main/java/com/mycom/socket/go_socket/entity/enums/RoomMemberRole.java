package com.mycom.socket.go_socket.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomMemberRole {
    OWNER("OWNER", "방장"),
    MEMBER("MEMBER", "멤버");

    private final String type;
    private final String description;

    public boolean isOwner() {
        return this == OWNER;
    }
}
