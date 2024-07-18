package com.vj.lets.web.cafe.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vj.lets.domain.cafe.dto.CafeOption;
import com.vj.lets.domain.cafe.dto.CafeSearch;
import com.vj.lets.domain.cafe.dto.SearchForm;
import com.vj.lets.domain.cafe.service.CafeService;
import com.vj.lets.domain.common.web.PageParams;
import com.vj.lets.domain.common.web.PageParamsForCafe;
import com.vj.lets.domain.common.web.Pagination;
import com.vj.lets.domain.common.web.PaginationForCafe;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.reservation.dto.Reservation;
import com.vj.lets.domain.reservation.service.ReservationService;
import com.vj.lets.domain.review.service.ReviewService;
import com.vj.lets.domain.room.dto.Room;
import com.vj.lets.domain.room.service.RoomService;
import com.vj.lets.domain.support.dto.FaqCategory;
import com.vj.lets.domain.support.service.FaqService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 카페 관련 요청을 처리하는 세부 컨트롤러 구현 클래스
 *
 * @author VJ특공대 강소영
 * @version 1.0
 * @since 2023. 9. 8.
 */
@Controller
//@RestController
@RequestMapping("/cafe")
@RequiredArgsConstructor
public class CafeController {

    private final ReservationService reservationService;
    private final CafeService cafeService;
    private final RoomService roomService;
    private final ReviewService reviewService;
    private final FaqService faqService;

    /**
     * 카페 메인 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     */
    @GetMapping("")
    public String cafeDetail(Model model) {
        List<Map<String, Object>> bestCafe = cafeService.getBestCafe();

        model.addAttribute("bestCafe", bestCafe);

        return "common/cafe/cafe_main";
    }

    /**
     * 카페 전체 리스트 출력
     *
     * @param page       페이지
     * @param searchForm 검색
     * @param model      모델 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     */
    @GetMapping("/list")
    public String cafeList(@PathParam("page") String page,
                           @ModelAttribute SearchForm searchForm,
                           Model model) {

        CafeSearch cafeSearch = CafeSearch.builder()
                .name(searchForm.getName())
                .option(searchForm.getOption())
                .build();

        if (searchForm.getCountPerson() != null && !searchForm.getCountPerson().isEmpty()) {
            cafeSearch.setCountPerson(Integer.parseInt(searchForm.getCountPerson()));
        }
        if (searchForm.getCurrentX() != null && !searchForm.getCurrentX().isEmpty()) {
            cafeSearch.setCurrentX(Long.parseLong(searchForm.getCurrentX()));
        }
        if (searchForm.getCurrentY() != null && !searchForm.getCurrentY().isEmpty()) {
            cafeSearch.setCurrentY(Long.parseLong(searchForm.getCurrentY()));
        }

        List<FaqCategory> categoryList = faqService.getCafeFaqList();
        List<CafeOption> options = cafeService.getOptionList();

        int elementSize = 8;
        int pageSize = 5;
        int rowCount = cafeService.getCountCafeList(cafeSearch);

        if (page == null || page.isEmpty()) {
            page = "1";
        }
        int selectPage = Integer.parseInt(page);
        PageParamsForCafe pageParamsForCafe = PageParamsForCafe.builder()
                .elementSize(elementSize)
                .pageSize(pageSize)
                .requestPage(selectPage)
                .rowCount(rowCount)
                .offset((elementSize * (selectPage - 1)))
                .cafeSearch(cafeSearch)
                .build();

        PaginationForCafe paginationForCafe = new PaginationForCafe(pageParamsForCafe);
        List<Map<String, Object>> allCafe = cafeService.getCafeList(pageParamsForCafe);

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("options", options);
        model.addAttribute("pagination", paginationForCafe);
        model.addAttribute("allCafe", allCafe);

        return "common/cafe/cafe_list";
    }

    /**
     * 카페 상세 페이지
     *
     * @param id          카페 ID
     * @param page        페이지
     * @param reservation 예약 객체
     * @param model       모델 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     * @author VJ특공대 박상훈
     */
    @GetMapping("/{id}")
    public String viewDetail(@PathVariable int id,
                             @PathParam("page") String page,
                             @ModelAttribute Reservation reservation,
                             Model model) {
        Map<String, Object> cafe = cafeService.getCafe(id);
        List<CafeOption> options = cafeService.getCafeOptionCafeId(id);
        List<Room> roomList = roomService.getSearchCafeRoom(id);

        int count = 0;

        if (cafe.get("reviewCount") != null) {
            count = Integer.parseInt(cafe.get("reviewCount").toString());
            //리뷰목록 페이징 처리
            int elementSize = 5;
            int pageSize = 5;

            if (page == null || page.isEmpty()) {
                page = "1";
            }

            int selectPage = Integer.parseInt(page);

            PageParams pageParams = PageParams.builder()
                    .elementSize(elementSize)
                    .pageSize(pageSize)
                    .requestPage(selectPage)
                    .rowCount(count)
                    .offset((elementSize * (selectPage - 1)))
                    .build();
            Pagination pagination = new Pagination(pageParams);
            List<Map<String, Object>> reviews = reviewService.getReviewListByCafe(id, pageParams);
            Map<Integer, Object> countReviews = reviewService.getCountReviewRatingByCafe(id);

            model.addAttribute("pagination", pagination);
            model.addAttribute("reviews", reviews);
            model.addAttribute("countReviews", countReviews);
        }

        model.addAttribute("Cafe", cafe);
        model.addAttribute("options", options);
        model.addAttribute("roomList", roomList);
        model.addAttribute("errorMessage", "");

        return "common/cafe/cafe_detail";
    }


    /**
     * 방 정보 및 가격, 예약 가능 시간 동적 출력
     *
     * @param id    방 ID
     * @param model 모델 객체
     * @return json으로 변환된 룸 리스트
     * @throws JsonProcessingException Json 처리 예외
     * @author VJ특공대 박상훈
     */
    @RequestMapping("/selectDate/{id}")
    @ResponseBody
    public String findRoom(@PathVariable int id, Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<Room> roomList = roomService.getSearchCafeRoom(id);

        return objectMapper.writeValueAsString(roomList);
    }

    /**
     * 중복예약을 막기 위해 패치로 중복 시간 제외
     *
     * @param roomId      룸 ID
     * @param model       모델 객체
     * @param bookingDate 예약 날짜
     * @return JSON에 반환된 이미 예약된 시간
     * @throws JsonProcessingException Json 처리 예외
     * @throws ParseException          파싱 예외
     */
    @PostMapping("/selectRoom/{roomId}")
    @ResponseBody
    public String findReservedTime(@PathVariable int roomId, Model model, @RequestBody Object bookingDate) throws JsonProcessingException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        String bookDate = bookingDate.toString();

        List<Reservation> reservedTime = reservationService.checkDuplicateResTime(roomId, bookDate);

        return objectMapper.writeValueAsString(reservedTime);
    }

    /**
     * 예약 생성하기
     *
     * @param id          카페 아이디
     * @param bookingDate 예약 날짜
     * @param headCount   예약 총원
     * @param startTime   시작 시간
     * @param endTime     종료 시간
     * @param roomId      룸 아이디
     * @param reservation 예약 정보
     * @param loginMember 로그인 회원 정보
     * @param model       모델 객체
     * @return 예약 ~결제 페이지
     * @author VJ 특공대 박상훈
     */
    @PostMapping("/{id}")
    public String bookCafe(@PathVariable("id") int id,
                           @RequestParam("bookingDate") String bookingDate,
                           @RequestParam("headCount") int headCount,
                           @RequestParam("startTime") int startTime,
                           @RequestParam("endTime") int endTime,
                           @RequestParam("roomId") int roomId,
                           @ModelAttribute Reservation reservation,
                           @SessionAttribute Member loginMember,
                           Model model) {

        // 예약 객체 설정
        reservation.setCafeId(id);
        reservation.setBookingDate(bookingDate);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setMemberId(loginMember.getId());
        reservation.setHeadCount(headCount);
        reservation.setRoomId(roomId);

        reservationService.reserve(reservation);
        int resId = reservationService.getNowRes(loginMember.getId());

        return "redirect:/reservation/" + resId;
    }
}