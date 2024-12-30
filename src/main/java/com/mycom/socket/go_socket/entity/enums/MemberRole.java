package com.mycom.socket.go_socket.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");  // ADIMN 오타 수정

    private final String key;
    private final String description;

    public String getAuthority() {
        return this.key;
    }
}
