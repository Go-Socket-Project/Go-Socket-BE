package com.mycom.socket.go_socket.entity;

import com.mycom.socket.global.common.BaseEntity;
import com.mycom.socket.go_socket.entity.enums.RoomMemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "chat_room_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private RoomMemberRole role;

    private LocalDateTime lastReadTime; // 마지막에 읽은 시간

    @Builder
    public ChatRoomMember(ChatRoom chatRoom, Member member, RoomMemberRole role) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.role = role;
        this.lastReadTime = LocalDateTime.now();
    }

    public void updateLastReadTime() {
        this.lastReadTime = LocalDateTime.now();
    }
}
