package com.example.api_memo.member.entity;

import java.util.ArrayList;
import java.util.List;


import com.example.api_memo.base.baseEntity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
    private String providerTypeCode; // 일반회원인지, 카카오로 가입한 회원인지, 네이버로 가입한 회원인지
    @Column(unique = true)
    private String username;
    private String password;
    private String nickname; // 추가된 필드
    private String bodytype; // 추가된 필드


    public boolean isAdmin() {
        return "admin".equals(username);
    }

    public void updateInfo(String nickname, String bodytype) {
        this.nickname = nickname;
        this.bodytype = bodytype;
    }
}