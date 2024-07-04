package com.vj.lets.domain.room.dto;

import lombok.*;

/**
 * 카페 룸 정보 수정 폼 객체
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
public class RoomEditForm {

    private int id;
    private String name;
    private String description;
    private int headCount;
    private int price;
    private int cafeId;

}
