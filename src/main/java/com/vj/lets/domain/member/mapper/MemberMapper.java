package com.vj.lets.domain.member.mapper;

import com.vj.lets.domain.member.dto.EditForm;
import com.vj.lets.domain.member.dto.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 회원 관련 매퍼 인터페이스
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@Mapper
public interface MemberMapper {

    /**
     * 회원 생성
     *
     * @param member 멤버 정보
     */
    public void create(Member member);

    /**
     * 전체 회원 목록 조회
     *
     * @return 회원 목록
     */
    public List<Member> readAll();

    /**
     * 이메일로 회원 암호화 비밀번호 조회
     *
     * @param email 이메일
     * @return 암호화 된 회원 비밀번호
     */
    public String readPasswordByEmail(String email);

    /**
     * 이메일로 회원 조회
     *
     * @param email 이메일
     * @return 회원 정보
     */
    public Member readByEmail(String email);

    /**
     * 이메일로 소셜 회원 조회
     * 
     * @param email 이메일
     * @return 회원 정보
     */
    public Member readSocialByEmail(String email);

    /**
     * ID로 회원 조회
     *
     * @param id 회원 ID
     * @return 회원 정보
     */
    public Member readById(int id);

    /**
     * 최근 1년간 월별 신규 회원 수 조회
     *
     * @return 신규 회원 수 목록
     */
    public List<Map<String, Object>> readCountByRegMonth();

    /**
     * 현재 가입 된 회원 성별 수 조회
     *
     * @return 회원 성별 수 목록
     */
    public List<Map<String, Object>> readCountByGender();

    /**
     * 최근 한 달간 신규 회원 수 조회
     *
     * @return 신규 회원 수
     */
    public int readCountByLastMonth();

    /**
     * 회원 수정 시 수정 정보 체크 용 폼 조회
     *
     * @param id 회원 ID
     * @return editForm 수정 정보 체크 용 폼 객체
     */
    public Member readUpdateForm(int id);

    /**
     * 회원 정보 수정
     *
     * @param member 회원 정보
     */
    public void update(Member member);

    /**
     * 회원 비활성화
     *
     * @param id 회원 ID
     */
    public void disabled(int id);

}







