package com.vj.lets.domain.member.mapper;

import com.vj.lets.domain.member.dto.EditForm;
import com.vj.lets.domain.member.dto.Member;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 회원 관련 매퍼 테스트 클래스
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@SpringBootTest
@Slf4j
class MemberMapperTest {

    @Autowired
    private MemberMapper memberMapper;

    @Test
    @Transactional
    void createTest() {
        // given
        Member member = Member.builder()
                .email("aaa@aaa.aaa")
                .name("가가가")
                .password("1111")
                .type("admin")
                .build();
        // when
        memberMapper.create(member);
        // then
        log.info("가입 회원 정보 : {}", member);
        assertThat(member).isNotNull();
    }

    @Test
    void readAllTest() {
        // given
        // when
        List<Member> list = memberMapper.readAll();
        // then
        log.info("회원 리스트 : {}", list);
        assertThat(list).isNotNull();
    }

    @Test
    void readByEmailTest() {
        // given
        String email = "lhy@gmail.com";
        // when
        Member member = memberMapper.readByEmail(email);
        // then
        log.info("회원 정보 : {}", member);
        assertThat(member).isNotNull();
    }

    @Test
    void readByIdTest() {
        // given
        int id = 1;
        // when
        Member member = memberMapper.readById(id);
        // then
        log.info("회원 정보 : {}", member);
        assertThat(member).isNotNull();
    }

    @Test
    void readCountByRegMonthTest() {
        // given
        // when
        List<Map<String, Object>> list = memberMapper.readCountByRegMonth();
        // then
        log.info("월별 회원 수 : {}", list);
        assertThat(list).isNotNull();
    }

    @Test
    void readCountByGenderTest() {
        // given
        // when
        List<Map<String, Object>> list = memberMapper.readCountByGender();
        // then
        log.info("성별 회원 수 : {}", list);
        assertThat(list).isNotNull();
    }

    @Test
    void readCountByLastMonthTest() {
        // given
        // when
        int count = memberMapper.readCountByLastMonth();
        // then
        log.info("신규 회원 수 : {}", count);
        assertThat(count).isNotZero();
    }

    @Test
    @Transactional
    void updateTest() {
        // given
        Member member = Member.builder()
                .id(1)
                .name("이이이")
                .password("1234")
//                .imagePath("aaa.jpg")
                .build();
        // when
        memberMapper.update(member);
        // then
        log.info("수정 회원 정보 : {}", member);
        assertThat(member).isNotNull();
    }

    @Test
    @Transactional
    void disabledTest() {
        // given
        int id = 1;
        // when
        memberMapper.disabled(id);
        // then
        log.info("삭제된 회원 아이디 : {}", id);
        assertThat(id).isNotZero();
    }
}