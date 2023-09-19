package com.vj.lets.domain.reservation.mapper;

import com.vj.lets.domain.common.web.PageParams;
import com.vj.lets.domain.reservation.dto.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 예약 Mapper 클래스
 * 작성일 (2023-09-07)
 *
 * @author 박상훈
 */
@Mapper
public interface ReservationMapper {

    //예약 생성
    public void create(Reservation reservation);

    /**
     * 예약 취소 시 비활성화
     *
     * @param id 예약 ID
     */
    public void cancel(int id);


    // 결제에 필요한 예약 정보 받아오기
    public Reservation findById(int id);

    /**
     * 예약 전체 리스트 조회
     *
     * @return 예약 리스트
     */
    public List<Reservation> findByAll();

    /**
     * 회원 ID로 예약 횟수 조회
     *
     * @param memberId 회원 아이디
     * @param type     검색 조건
     * @return 예약 횟수
     * @see com.vj.lets.web.dashboard.controller.MypageController
     */
    public int readCountByMember(@Param("memberId") int memberId, @Param("type") String type);

    /**
     * 회원 ID로 예약 목록 조회
     *
     * @param memberId   회원 ID
     * @param pageParams 페이징 객체
     * @return 예약 목록
     * @see com.vj.lets.web.dashboard.controller.MypageController
     */
    public List<Map<String, Object>> findByMember(@Param("memberId") int memberId, @Param("pageParams") PageParams pageParams);

    //카페 id로 예약 전체 리스트 조회
    public List<Reservation> findCafeResList(int cafeId);

    //매달 총 예약 건 수 가져오기
    public List<Map<String, Object>> findCountMonthRes(int cafeId);

    //d
    public List<Map<String, Reservation>> findTotalData(int cafeId);


    //예약에 필요한 예약 정보 가져오기
    public Map<String, Reservation> findResInfo(int id);

    //예약 - 결제 이동을 위해 필요
    public int findNowRes(int memberId);

    //예약 중복 방지
    public int checkDuplicateReservation(@Param("roomId") int roomId, @Param("bookingDate") String bookingDate, @Param("startTime") int startTime, @Param("endTime") int endTime);

    //예약 중복 방지
    public List<Reservation> checkDuplicateResTime(@Param("roomId") int roomId, @Param("bookingDate") String bookingDate);


    //예약 삭제
    public void delete(@Param("id") int id, @Param("memberId") int memberId);
}
