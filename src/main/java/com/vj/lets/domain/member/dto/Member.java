package com.vj.lets.domain.member.dto;

import lombok.*;

/**
 * 회원 DTO Bean
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Member {

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
    private int fromSocial;

}





