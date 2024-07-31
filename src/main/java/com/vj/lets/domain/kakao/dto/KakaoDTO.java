package com.vj.lets.domain.kakao.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class KakaoDTO {

    private String code;
    private Long   id;
    private String name;
    private String email;
    private String phone;

    private String memberEmail;
    private String memberName;
    private String memberSeq;

}
