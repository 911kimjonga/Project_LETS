package com.vj.lets.domain.member.service;

import com.vj.lets.domain.cafe.mapper.CafeHistoryMapper;
import com.vj.lets.domain.cafe.mapper.CafeMapper;
import com.vj.lets.domain.cafe.mapper.CafeOptionListMapper;
import com.vj.lets.domain.group.dto.GroupMemberList;
import com.vj.lets.domain.group.mapper.GroupHistoryMapper;
import com.vj.lets.domain.group.mapper.GroupMemberListMapper;
import com.vj.lets.domain.group.mapper.StudyGroupMapper;
import com.vj.lets.domain.group.util.PositionType;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.member.mapper.MemberHistoryMapper;
import com.vj.lets.domain.member.mapper.MemberMapper;
import com.vj.lets.domain.member.util.MemberHistoryComment;
import com.vj.lets.domain.member.util.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 회원 관련 비즈니스 로직 처리 및 트랜잭션 관리 서비스 구현체
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberHistoryMapper memberHistoryMapper;
    private final CafeMapper cafeMapper;
    private final CafeOptionListMapper cafeOptionListMapper;
    private final CafeHistoryMapper cafeHistoryMapper;
    private final StudyGroupMapper studyGroupMapper;
    private final GroupHistoryMapper groupHistoryMapper;
    private final GroupMemberListMapper groupMemberListMapper;

    /**
     * 회원 가입
     *
     * @param member 가입 회원 정보
     */
    @Override
    @Transactional
    public void register(Member member) {
        memberMapper.create(member);
        memberHistoryMapper.create();
    }

    /**
     * 로그인 시 회원 유무 조회를 위한 암호화 비밀번호 조회
     *
     * @param email    이메일
     * @return 로그인 한 회원 정보
     */
    @Override
    public String isMember(String email) {
        return memberMapper.readPasswordByEmail(email);
    }

    /**
     * 비밀번호 암호화 처리 기능
     *
     * @param inputValue 입력 비밀번호
     * @return 암호화 처리 된 비밀번호
     */
    public String encodeBcrypt(String inputValue, int strength) {
        return new BCryptPasswordEncoder(strength).encode(inputValue);
    }

    /**
     * 암호화 비밀번호 비교 기능
     *
     * @param inputValue 암호화된 입력 비밀번호
     * @param compareValue 암호화된 비교 비밀번호
     * @return 일치 여부
     */
    public boolean matchesBcrypt(String inputValue, String compareValue, int strength) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(strength);
        return passwordEncoder.matches(inputValue, compareValue);
    }

    /**
     * 이메일 중복 체크 조회 및 로그인 시 회원 조회
     *
     * @param email 이메일
     * @return 이메일로 조회한 회원 정보
     */
    @Override
    public Member getMemberByEmail(String email) {
        return memberMapper.readByEmail(email);
    }

    @Override
    public Member getSocialMemberByEmail(String email) {
        return memberMapper.readSocialByEmail(email);
    }

    /**
     * 전체 회원 목록 조회
     *
     * @return 전체 회원 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public List<Member> getMemberList() {
        return memberMapper.readAll();
    }

    /**
     * 특정 회원 정보 조회
     *
     * @param id 회원 ID
     * @return 조회한 회원 정보
     * @see com.vj.lets.web.dashboard.controller.MypageController
     */
    @Override
    public Member getMember(int id) {
        return memberMapper.readById(id);
    }

    /**
     * 최근 1년간 월별 신규 회원 수 조회
     *
     * @return 월별 신규 회원 수 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public List<Map<String, Object>> getCountByRegMonth() {
        return memberMapper.readCountByRegMonth();
    }

    /**
     * 현재 가입 된 회원 성별 수 조회
     *
     * @return 회원 성별 수 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public List<Map<String, Object>> getCountByGender() {
        return memberMapper.readCountByGender();
    }

    /**
     * 최근 한 달간 가입한 신규 회원 수 조회
     *
     * @return 신규 회원 수
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public int getCountByLastMonth() {
        return memberMapper.readCountByLastMonth();
    }

    /**
     * 회원 정보 수정 시 수정 정보 중복 체크 용 폼 조회
     *
     * @param id 회원 ID
     * @return 체크 용 폼 객체
     * @see com.vj.lets.web.dashboard.controller.MypageController
     */
    @Override
    public Member checkEdit(int id) {
        return memberMapper.readUpdateForm(id);
    }

    /**
     * 회원 정보 수정
     *
     * @param member 회원 정보
     */
    @Override
    @Transactional
    public void editMember(Member member) {
        memberMapper.update(member);
        memberHistoryMapper.createByUpdate(member.getId(), MemberHistoryComment.UPDATE.getComment());
    }

    /**
     * 회원 탈퇴
     *
     * @param id 회원 ID
     */
    @Override
    @Transactional
    public void removeMember(int id) {
        Member member = memberMapper.readById(id);
        String memberType = member.getType();

        if (memberType.equals(MemberType.HOST.getType())) {
            Map<String, Object> cafe = cafeMapper.findByMemberId(id);
            int cafeId = Integer.parseInt(cafe.get("id").toString());

            cafeMapper.delete(cafeId);
            cafeOptionListMapper.delete(cafeId);
            cafeHistoryMapper.delete(cafeId);

        } else if (memberType.equals(MemberType.GUEST.getType())) {
            List<Map<String, Object>> myGroupList = groupMemberListMapper.findMyGroupList(id);
            for (Map<String, Object> map : myGroupList) {
                int groupId = Integer.parseInt(map.get("STUDYGROUPID").toString());
                GroupMemberList memberList = groupMemberListMapper.isGroupMember(id, groupId);
                String position = memberList.getPosition();

                groupMemberListMapper.removeMember(id, groupId);
                studyGroupMapper.minusCurrentCount(groupId);

                if (position.equals(PositionType.LEADER.getType())) {
                    if (groupMemberListMapper.readGroupMemberCount(groupId)) {
                        int oldestMemberId = groupMemberListMapper.readOldestMemberByGroupId(groupId);

                        groupMemberListMapper.updateMemberPosition(oldestMemberId, groupId);
                        groupHistoryMapper.updateGroupHistory(groupId);
                    } else {
                        studyGroupMapper.deleteStudy(groupId);
                        groupHistoryMapper.deleteGroupHistory(groupId);
                    }

                } else if (position.equals(PositionType.MEMBER.getType())) {
                    groupHistoryMapper.updateGroupHistory(groupId);
                }

            }

        }

        memberMapper.disabled(id);
        memberHistoryMapper.createByUpdate(id, MemberHistoryComment.DELETE.getComment());

    }
    
}
