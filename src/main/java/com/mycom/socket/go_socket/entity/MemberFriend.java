package com.mycom.socket.go_socket.entity;

import com.mycom.socket.global.common.BaseEntity;
import com.mycom.socket.go_socket.entity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberFriend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private Member friend;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @Builder
    public MemberFriend(Member member, Member friend) {
        this.member = member;
        this.friend = friend;
        this.status = FriendStatus.PENDING;
    }

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
    }

    public void block() {
        this.status = FriendStatus.BLOCKED;
    }
}
