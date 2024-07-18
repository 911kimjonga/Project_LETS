package com.vj.lets.domain.common.web;

import lombok.*;

/**
 * 페이징 정보 클래스
 *
 * @author 김종원
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

}