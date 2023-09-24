package com.vj.lets.domain.common.web;

import com.vj.lets.domain.cafe.dto.CafeSearch;
import lombok.*;

/**
 * 카페 출력을 위한 페이징 정보 클래스
 *
 * @author VJ특공대 강소영
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class PageParamsForCafe {

    // 페이지에 보여지는 목록 갯수
    private int elementSize;

    // 페이지에 보여지는 페이지 갯수
    private int pageSize;

    // 사용자 요청 페이지
    private int requestPage;

    // 테이블 목록 갯수
    private int rowCount;

    //카페 검색
    private CafeSearch cafeSearch;

}