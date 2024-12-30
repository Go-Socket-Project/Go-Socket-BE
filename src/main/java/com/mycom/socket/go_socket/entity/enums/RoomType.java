package com.mycom.socket.go_socket.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {

    INDIVIDUAL("INDIVIDUAL", "1:1 채팅"),
    GROUP("GROUP", "그룹 채팅");

    private final String type;
    private final String description;
}
