package com.vj.lets.domain.reservation.service;

import com.vj.lets.domain.reservation.dto.Reservation;

import java.util.List;
import java.util.Map;

/**
 * 예약 서비스 클래스
 * @author 박상훈
 * @version 1.0
 * @since 2023-09-10 (일)
 */
public interface ReservationService {

    /**
     * 예약하기 create
     * @param reservation
     */
    public void reserve(Reservation reservation);

    /**
     * 예약아이디로 예약 찾기
     * @param id
     * @return 해당 예약
     */
    public Reservation findById(int id);

    /**
     * 예약 전체 리스트 조회
     * @return 예약 리스트
     */
   public List<Reservation> getReservationList();

    //회원 Id 로 예약 전체 리스트 조회
    public List<Reservation> getMemberResList(int memberId);

    //카페 id로 예약 전체 리스트 조회
    public List<Reservation> getCafeResList(int cafeId);

    //매달 총 예약 건수 가져오기
    public List<Map<String, Object>> getCountMonthRes(int cafeId);

    //호스트의 모든 예약 데이터 가져오기
    public List<Map<String,Reservation>> getTotalData(int cafeId);


}