package com.mycom.socket.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class SoftDeleteEntity extends BaseEntity {

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(name = "deleted_at")  // DB 컬럼명 명시적 지정
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
