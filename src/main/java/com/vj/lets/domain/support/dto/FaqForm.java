package com.vj.lets.domain.support.dto;

import lombok.*;

/**
 * FAQ 폼 객체
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-14 (목)
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class FaqForm {

    int faqId;
    String title;
    int category;
    String content;

}
