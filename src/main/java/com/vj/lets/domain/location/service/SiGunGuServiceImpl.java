package com.vj.lets.domain.location.service;

import com.vj.lets.domain.location.dto.SiGunGu;
import com.vj.lets.domain.location.mapper.SiGunGuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 시,군,구 서비스 구현체
 *
 * @author VJ특공대 이희영
 * @version 1.0
 * @since 2023-09-11 (월)
 */
@RequiredArgsConstructor
@Service
public class SiGunGuServiceImpl implements SiGunGuService {

    private final SiGunGuMapper siGunGuMapper;

    /**
     * 시,군,구 조회
     *
     * @param siGunGuName 시,군,구 이름
     * @return 조회된 시,군,구 정보
     * @author VJ특공대 이희영
     */
    @Override
    public SiGunGu findById(String siGunGuName) {
        return siGunGuMapper.getSiGunGu(siGunGuName);
    }

    /**
     * 이름으로 시군구 ID 조회
     *
     * @param siGunGuName 시군구 이름
     * @param siDoName    시도 이름
     * @return 시군구 ID
     * @author VJ특공대 강소영
     */
    public int getSiGunGuDo(String siGunGuName, String siDoName) {
        return siGunGuMapper.getSiGunGuDo(siGunGuName, siDoName);
    }
}
