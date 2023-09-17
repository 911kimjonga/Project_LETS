package com.vj.lets.domain.location.service;

import com.vj.lets.domain.location.dto.SiGunGu;

/**
 * 시,군,구 서비스 규약
 *
 * @author VJ특공대 이희영
 * @version 1.0
 * @since 2023-09-11 (월)
 */
public interface SiGunGuService {

    /**
     * 시,군,구 조회
     *
     * @author VJ특공대 이희영
     * @param siGunGuName
     * @return 조회된 시,군,구 정보
     */
    public SiGunGu findById(String siGunGuName);
}