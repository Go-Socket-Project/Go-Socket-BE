package com.mycom.socket.member.domain;

import com.mycom.socket.global.domain.BaseEntity;
import com.mycom.socket.member.domain.type.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    private String intro;

    @Column(name = "profile_image_url")
    private String profileImgUrl;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

}
