package com.vj.lets.domain.member.service;

import com.vj.lets.domain.member.dto.EditForm;
import com.vj.lets.domain.member.dto.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

/**
 * 회원 관련 비즈니스 로직 처리 및 트랜잭션 관리 서비스 인터페이스
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
public interface MemberService {

    /**
     * 회원 가입
     *
     * @param member 가입 회원 정보
     */
    public void register(Member member);

    /**
     * 로그인 시 회원 유무 조회를 위한 암호화 비밀번호 조회
     *
     * @param email    이메일
     * @return 로그인 한 회원 정보
     */
    public String isMember(String email);

    /**
     * 비밀번호 암호화 처리
     *
     * @param inputValue 입력 비밀번호
     * @return 암호화 처리 된 비밀번호
     */
    public String encodeBcrypt(String inputValue, int strength);

    /**
     * 암호화 비밀번호 비교
     *
     * @param inputValue 암호화된 입력 비밀번호
     * @param compareValue 암호화된 비교 비밀번호
     * @return 일치 여부
     */
    public boolean matchesBcrypt(String inputValue, String compareValue, int strength);

    /**
     * 이메일 중복 체크 조회 및 로그인 시 회원 조회
     *
     * @param email 이메일
     * @return 이메일로 조회한 회원 정보
     */
    public Member getMemberByEmail(String email);

    /**
     * 이메일로 소셜 회원 조회
     * 
     * @param email 이메일
     * @return 조회한 회원 정보
     */
    public Member getSocialMemberByEmail(String email);

    /**
     * 전체 회원 목록 조회
     *
     * @return 전체 회원 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    public List<Member> getMemberList();

    /**
     * 특정 회원 정보 조회
     *
     * @param id 회원 ID
     * @return 조회한 회원 정보
     * @see com.vj.lets.web.dashboard.controller.MypageController
     */
    public Member getMember(int id);

    /**
     * 최근 1년간 월별 신규 회원 수 조회
     *
     * @return 월별 신규 회원 수 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    public List<Map<String, Object>> getCountByRegMonth();

    /**
     * 현재 가입 된 회원 성별 수 조회
     *
     * @return 회원 성별 수 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    public List<Map<String, Object>> getCountByGender();

    /**
     * 최근 한 달간 가입한 신규 회원 수 조회
     *
     * @return 신규 회원 수
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    public int getCountByLastMonth();

    /**
     * 회원 정보 수정 시 수정 정보 중복 체크 용 폼 조회
     *
     * @param id 회원 ID
     * @return 체크 용 폼 객체
     * @see com.vj.lets.web.dashboard.controller.MypageController
     */
    public Member checkEdit(int id);

    /**
     * 회원 정보 수정
     *
     * @param member 회원 정보
     */
    public void editMember(Member member);

    /**
     * 회원 탈퇴
     *
     * @param id 회원 ID
     */
    public void removeMember(int id);


}
