package com.vj.lets.domain.support.service;

import com.vj.lets.domain.cafe.dto.Cafe;
import com.vj.lets.domain.common.web.PageParams;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.support.dto.Contact;
import com.vj.lets.domain.support.dto.ContactForm;

import java.util.List;

/**
 * 입점 신청 관련 비즈니스 로직 처리 및 트랜잭션 관리 서비스 인터페이스
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-10 (일)
 */
public interface ContactService {

    /**
     * 신규 입점 신청 등록
     *
     * @param contact 입점 신청 정보
     */
    public void register(Contact contact);

    /**
     * 상태로 전체 입점 신청 갯수 조회
     *
     * @param type 입점 신청 상태
     * @return 해당 상태의 입점 신청 갯수
     */
    public int getCountContact(String type);

    /**
     * 전체 입점 신청 목록 조회
     *
     * @param pageParams 페이징 정보
     * @return 입점 신청 목록
     */
    public List<Contact> getContactList(PageParams pageParams);

    /**
     * 입점 신청 ID로 특정 입점 신청 정보 조회
     *
     * @param id 입점 신청 ID
     * @return 입점 신청 정보
     */
    public Contact getContact(int id);

    /**
     * 입점 신청 시 이메일, 사업자 번호, 신청 날짜로 중복 신청 조회
     *
     * @param contactForm 입점 신청 입력 폼
     * @return 입점 신청 정보
     */
    public List<Contact> checkContact(ContactForm contactForm);

    /**
     * 입점 승인 시 회원 등록, 카페 개설 및 입점 신청 상태 정보 수정
     *
     * @param id     승인할 입점 신청 ID
     * @param member 회원 가입 정보
     * @param cafe   카페 생성 정보
     */
    public void approveContact(int id, Member member, Cafe cafe);

    /**
     * 입점 거부 시 상태 정보 수정
     *
     * @param id 거부할 입점 신청 ID
     */
    public void refuseContact(int id);

}
