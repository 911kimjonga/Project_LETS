package com.vj.lets.domain.cafe.dto;

import lombok.*;

/**
 * 카페 검색 폼 객체
 *
 * @author VJ특공대 강소영
 * @version 1.0
 * @since 2023-09-11 (월)
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class SearchForm {

    String name;
    String countPerson;
    String option;
    String currentX;
    String currentY;

}
