package com.vj.lets.domain.cafe.service;

import com.vj.lets.domain.cafe.dto.Cafe;
import com.vj.lets.domain.cafe.dto.CafeOption;
import com.vj.lets.domain.cafe.dto.CafeOptionList;
import com.vj.lets.domain.cafe.dto.CafeSearch;
import com.vj.lets.domain.cafe.mapper.CafeHistoryMapper;
import com.vj.lets.domain.cafe.mapper.CafeMapper;
import com.vj.lets.domain.cafe.mapper.CafeOptionListMapper;
import com.vj.lets.domain.cafe.mapper.CafeOptionMapper;
import com.vj.lets.domain.common.web.PageParams;
import com.vj.lets.domain.common.web.PageParamsForCafe;
import com.vj.lets.domain.location.mapper.SiGunGuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Cafe Service 구현체
 *
 * @author VJ특공대 강소영
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@RequiredArgsConstructor
@Service
public class CafeServiceImpl implements CafeService{

    private final CafeMapper cafeMapper;
    private final CafeHistoryMapper cafeHistoryMapper;
    private final CafeOptionMapper cafeOptionMapper;
    private final CafeOptionListMapper cafeOptionListMapper;
    private final SiGunGuMapper siGunGuMapper;

    /**
     * 카페 옵션 등록
     *
     * @param cafeOptionLists 카페 옵션 목록
     */
    @Override
    @Transactional
    public void cafeOptionRegister(List<CafeOptionList> cafeOptionLists) {
        for (CafeOptionList list : cafeOptionLists) {
            cafeOptionListMapper.create(list);
        }
    }

    /**
     * 카페 옵션 등록
     *
     * @param cafeOption 카페 옵션
     */
    @Override
    public void optionRegister(CafeOption cafeOption) {
        cafeOptionMapper.create(cafeOption);
    }

    /**
     * 카페 옵션 삭제
     *
     * @param optionId 카페 옵션 ID
     */
    @Override
    public void optionDelete(int optionId) {
        cafeOptionMapper.delete(optionId);
    }

    /**
     * 카페 전체 목록 출력
     *
     * @param pageParamsForCafe 페이징 정보
     * @return 카페 전체 목록
     */
    @Override
    public List<Map<String, Object>> getCafeList(PageParamsForCafe pageParamsForCafe) {
        return cafeMapper.findByAll(pageParamsForCafe);
    }

    /**
     * 카페 목록 개수 출력
     *
     * @param cafeSearch 카페 검색
     * @return 카페 개수
     */
    @Override
    public int getCountCafeList(CafeSearch cafeSearch) {
        return cafeMapper.countByCafeList(cafeSearch);
    }

    /**
     * 카페 옵션 목록 출력
     *
     * @return 카페 옵션 전체 목록
     */
    @Override
    public List<CafeOption> getOptionList() {
        return cafeOptionMapper.readAll();
    }

    /**
     * 카페 ID로 카페 검색
     *
     * @param id 카페 ID
     * @return 카페 검색 결과
     */
    @Override
    public Map<String, Object> getCafe(int id) {
        return cafeMapper.findById(id);
    }

    /**
     * 회원 ID로 카페 검색
     *
     * @param id 회원 ID
     * @return 카페 검색 결과
     */
    @Override
    public Map<String, Object> getCafeMemberId(int id) {
        return cafeMapper.findByMemberId(id);
    }

    /**
     * 카페 ID로 옵션 목록 검색
     *
     * @param id 카페 ID
     * @return 옵션 목록
     */
    @Override
    public List<CafeOption> getCafeOptionCafeId(int id) {
        return cafeOptionListMapper.findByOptionCafeId(id);
    }

    /**
     * 예약이 많은 카페 6개 검색
     *
     * @return 카페 목록 6개
     */
    @Override
    public List<Map<String, Object>> getBestCafe(){
        return cafeMapper.findByBest();
    }

    /**
     * 카페 검색
     *
     * @param cafeSearch        카페 검색값
     * @param pageParamsForCafe 페이징 정보
     * @return 카페 검색 결과
     */
    @Override
    public List<Map<String, Object>> getSearchCafe(CafeSearch cafeSearch, PageParamsForCafe pageParamsForCafe){
        return cafeMapper.findBySearch(cafeSearch, pageParamsForCafe);
    }

    /**
     * 전체 입점 카페 수 조회
     *
     * @return 전체 입점 카페 수
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public int getCountCafeForAdmin() {
        return cafeMapper.readCountAllForAdmin();
    }

    /**
     * 관리자 용 전체 카페 목록 조회
     *
     * @param pageParams 페이징 정보
     * @return 카페 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public List<Map<String, Object>> getCafeListForAdmin(PageParams pageParams) {
        return cafeMapper.readAllForAdmin(pageParams);
    }

    /**
     * 최근 1년간 월별 신규 카페 수 조회
     *
     * @return 신규 카페 수 목록
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public List<Map<String, Object>> getCountByRegMonth() {
        return cafeMapper.readCountByRegMonth();
    }

    /**
     * 최근 한 달간 등록된 신규 카페 수 조회
     *
     * @return 신규 카페 수
     * @see com.vj.lets.web.dashboard.controller.AdminController
     */
    @Override
    public int getCountByLastMonth() {
        return cafeMapper.readCountByLastMonth();
    }

    /**
     * 카페 정보 수정
     *
     * @param cafeId    카페 ID
     * @param siGunGu   시군구 이름
     * @param siDo      시도 이름
     * @param cafe      카페 정보
     * @param comment   수정 사유
     * @param optionIds 수정한 옵션 목록
     */
    @Override
    @Transactional
    public void editCafe(int cafeId, String siGunGu, String siDo, Cafe cafe, String comment, List<Integer> optionIds) {
        List<CafeOptionList> cafeOptionLists = new ArrayList<>();
        for (int optionId : optionIds) {
            CafeOptionList makeList = CafeOptionList
                    .builder()
                    .cafeId(cafeId)
                    .optionId(optionId)
                    .build();
            cafeOptionLists.add(makeList);
        }

        if (siGunGu != null){
            int sggId = siGunGuMapper.getSiGunGuDo(siGunGu, siDo);
            cafe.setSiGunGuId(sggId);
        }

        cafeMapper.update(cafe);
        cafeHistoryMapper.update(comment, cafe.getId());
        cafeOptionListMapper.delete(cafe.getId());
        for (CafeOptionList list : cafeOptionLists) {
            cafeOptionListMapper.create(list);
        }
    }

    /**
     * 카페 옵션 여부 체크
     *
     * @param cafeId   카페 ID
     * @param optionId 옵션 ID
     * @return 옵션 여부 체크
     */
    @Override
    public boolean cafeOptionCheck(int cafeId, int optionId) {
        return cafeOptionListMapper.findByOptionCheckCafeId(cafeId, optionId);
    }


}
