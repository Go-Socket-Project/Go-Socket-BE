package com.mycom.socket.go_socket.entity;

import com.mycom.socket.global.common.BaseEntity;
import com.mycom.socket.go_socket.entity.enums.RoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    @Column(unique = true)
    private String roomCode;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;  // 개인 & 그룹

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatRoomMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();

    @Builder
    public ChatRoom(String roomName, String roomCode, RoomType roomType) {
        this.roomName = roomName;
        this.roomCode = roomCode;
        this.roomType = roomType;
    }

    public void addMember(ChatRoomMember member) {
        this.members.add(member);
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }
}
