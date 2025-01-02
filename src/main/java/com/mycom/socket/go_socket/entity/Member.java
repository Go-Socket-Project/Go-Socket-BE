package com.mycom.socket.go_socket.entity;

import com.mycom.socket.global.common.BaseEntity;
import com.mycom.socket.go_socket.entity.enums.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickname;
    private String password;
    private String intro; // 한 줄 소개
    private String refreshToken; // 리프레시 토큰

    @Column(name = "profile_image_url")
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberFriend> friends;

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}
