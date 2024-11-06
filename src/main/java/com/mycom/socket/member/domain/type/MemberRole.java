package com.mycom.socket.member.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    USER("ROLE_USER");

    private final String key;
}
