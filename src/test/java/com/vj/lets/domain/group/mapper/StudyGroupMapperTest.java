package com.vj.lets.domain.group.mapper;

import com.vj.lets.domain.group.dto.StudyGroup;
import com.vj.lets.domain.group.util.PageParams;
import com.vj.lets.domain.location.dto.SiGunGu;
import com.vj.lets.domain.location.mapper.SiGunGuMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스터디 그룹 매퍼 테스트
 *
 * @author VJ특공대 이희영
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@SpringBootTest
@Slf4j
class StudyGroupMapperTest {

    @Autowired
    private StudyGroupMapper studyGroupMapper;
    @Autowired
    private SiGunGuMapper siGunGuMapper;

    @Test
    @Transactional
    void createTest() {
        // given
        StudyGroup studyGroup = StudyGroup.builder()
                .name("스터디")
                .totalCount(10)
                .subject("주식")
                .imagePath("")
                .build();

        String siGunGuName = "노원구";
        SiGunGu siGunGu = siGunGuMapper.getSiGunGu(siGunGuName);

        studyGroup.setSiGunGuId(siGunGu.getId());

        // when
        studyGroupMapper.createStudy(studyGroup);

        // then
        log.info("생성한 스터디 그룹 정보 : {}", studyGroup);
    }

    @Test
    @Transactional
    void findIdTest() {
        // given
        StudyGroup studyGroup = StudyGroup.builder()
                .name("스터디")
                .totalCount(10)
                .subject("주식")
                .build();

        String siGunGuName = "노원구";
        SiGunGu siGunGu = siGunGuMapper.getSiGunGu(siGunGuName);

        studyGroup.setSiGunGuId(siGunGu.getId());

        // when
        studyGroupMapper.createStudy(studyGroup);
        int studyGroupId = studyGroupMapper.findByStudyId();

        // then
        log.info("현재 스터디 그룹 아이디 정보 : {}", studyGroupId);
    }

    @Test
    @Transactional
    void findAllTest() {
        // given
        String keyword = "테스트";
        String subject = "";

        int count = studyGroupMapper.studySearchCount(keyword, subject);

        PageParams pageParams = PageParams.builder()
                .elementSize(5)
                .pageSize(5)
                .requestPage(1)
                .rowCount(count)
                .keyword(keyword)
                .build();

        // when
        List<Map<String, Object>> studyGroupList = studyGroupMapper.findAllStudyList(pageParams);

        // then
        log.info("스터디 그룹 전체 리스트 : {}", studyGroupList);
        assertThat(studyGroupList).isNotNull();
    }

    @Test
    @Transactional
    void readTest() {
        // given
        int studyGroupId = 1;

        // when
        Map<String, Object> studyGroup = studyGroupMapper.readStudy(studyGroupId);

        // then
        log.info("조회된 스터디 그룹 정보 : {}", studyGroup);

        assertThat(studyGroup).isNotNull();
    }

    @Test
    @Transactional
    void updateTest() {
        // given
        StudyGroup studyGroup = StudyGroup.builder()
                .id(1)
                .name("이름업데이트")
                .totalCount(10)
                .subject("주제")
                .build();
        log.info("테스트 : {}", studyGroup);

        // when
        studyGroupMapper.updateStudy(studyGroup);

        // then
        log.info("스터디 그룹 수정 완료");
    }

    @Test
    @Transactional
    void deleteTest() {
        // given
        int studyGroupId = 1;

        // when
        studyGroupMapper.deleteStudy(studyGroupId);

        // then
        log.info("스터디 그룹 삭제 완료");
    }

    @Test
    @Transactional
    void addTest() {
        // given
        int studyGroupId = 1;

        // when
        studyGroupMapper.plusCurrentCount(studyGroupId);

        // then
        log.info("스터디 그룹 회원 수 증가 완료");
    }

    @Test
    @Transactional
    void subtractTest() {
        // given
        int studyGroupId = 1;

        // when
        studyGroupMapper.minusCurrentCount(studyGroupId);

        // then
        log.info("스터디 그룹 회원 수 감소 완료");
    }

    @Test
    @Transactional
    void findNewStudyListTest() {
        // given

        // when
        List<StudyGroup> newStudyList = studyGroupMapper.findNewStudyList();

        // then
        log.info("신규 스터디 리스트 : {}", newStudyList);
        assertThat(newStudyList).isNotNull();
    }

    @Test
    @Transactional
    void studySearchCountTest() {
        // given
        String keyword ="테스트";
        String subject = "";

        // when
        int count = studyGroupMapper.studySearchCount(keyword, subject);

        // then
        log.info("검색 결과 수 : {}", count);
        assertThat(count).isNotZero();
    }
}