package com.unikraft.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    @Column(unique = true)
    private String loginId;
    @Column(unique = true)
    private String email;
    private String password;
    private String address;

    @Builder
    public Member(String name, int age, String loginId, String email, String password, String address){
        this.name = name;
        this.age = age;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.address = address;
    }
}