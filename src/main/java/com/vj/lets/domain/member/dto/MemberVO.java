package com.vj.lets.domain.member.dto;

import lombok.*;

/**
 * 회원 VO
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
public class MemberVO {

    private int id;
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
    private String birthday;
    private String gender;
    private String regdate;
    private String type;
    private String imagePath;
    private String status;
    private String fromSocial;

    public MemberVO(int id, String email, String name, String type, String imagePath) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type = type;
        this.imagePath = imagePath;
    }

    public MemberVO(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public MemberVO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public MemberVO(String name, String password, String phoneNumber, String gender, String birthday) {
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthday = birthday;
    }
}





