package com.vj.lets.domain.group.util;

import lombok.*;

/**
 * 스터디 그룹 전용 페이징 객체
 *
 * @author VJ특공대 이희영
 * @version 1.0
 * @since 2023-09-20 (수)
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class PageParams {

    // 페이지에 보여지는 목록 갯수
    private int elementSize;

    // 페이지에 보여지는 페이지 갯수
    private int pageSize;

    // 사용자 요청 페이지
    private int requestPage;

    // 테이블 목록 갯수
    private int rowCount;

    // 오프셋
    private int offset;

    // 검색 타입
    private String type;

    // 검색 키워드
    private String keyword;

    // 검색 시,군,구 이름
    private String siGunGuName;

    // 검색 주제
    private String subject;

//    //페이징 처리를위한 그룹아이디
//    private  int groupId;

}