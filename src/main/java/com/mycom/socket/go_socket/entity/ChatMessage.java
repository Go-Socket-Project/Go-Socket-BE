package com.mycom.socket.go_socket.entity;


import com.mycom.socket.global.common.SoftDeleteEntity;
import com.mycom.socket.go_socket.entity.enums.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;    // 이미지 메시지의 경우

    @Builder
    public ChatMessage(ChatRoom chatRoom, Member sender, MessageType type,
                       String content, String imageUrl) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.type = type;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
