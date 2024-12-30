package com.mycom.socket.go_socket.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    ENTER("ENTER", "입장"),    // 채팅방 입장
    TALK("TALK", "메시지"),    // 일반 텍스트 메시지
    LEAVE("LEAVE", "퇴장"),    // 채팅방 퇴장
    IMAGE("IMAGE", "이미지");  // 이미지 전송

    private final String type;
    private final String description;

    public static MessageType of(String type) {
        return Arrays.stream(values())
                .filter(messageType -> messageType.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid message type: " + type));
    }

    public boolean isSystemMessage() {
        return this == ENTER || this == LEAVE;
    }

    public boolean isMediaMessage() {
        return this == IMAGE;
    }
}
