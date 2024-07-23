package com.vj.lets.domain.member.util;

import lombok.Getter;

/**
 * 비밀번호 암호화 강도 Enum 클래스
 *
 * @author 김종원
 * @version 1.0
 * @since 24. 7. 23. (화)
 */
@Getter
public enum MemberCrypt {
    STRENGTH(10);

    private final int strength;

    private MemberCrypt(int strength) {
        this.strength = strength;
    }

}
