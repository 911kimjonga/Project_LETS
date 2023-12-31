package com.vj.lets.domain.cafe.dto;

import lombok.*;

/**
 * 카페 옵션 DTO
 *
 * @author VJ특공대 강소영
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class CafeOption {

    private int id;
    private String name;
    private String imagePath;
    private String description;

}
