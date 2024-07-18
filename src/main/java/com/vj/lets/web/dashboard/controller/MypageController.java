package com.vj.lets.web.dashboard.controller;

import com.vj.lets.domain.common.web.PageParams;
import com.vj.lets.domain.common.web.Pagination;
import com.vj.lets.domain.member.dto.EditForm;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.member.service.MemberService;
import com.vj.lets.domain.reservation.service.ReservationService;
import com.vj.lets.domain.review.dto.Review;
import com.vj.lets.domain.review.dto.ReviewForm;
import com.vj.lets.domain.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 회원 마이페이지 관련 요청 컨트롤러
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    private static final int ELEMENT_SIZE = 3;
    private static final int PAGE_SIZE = 3;

    /**
     * 마이페이지 메인 화면 출력
     *
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("")
    public String mypageMain(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        Member member = memberService.getMember(loginMember.getId());

        EditForm editForm = EditForm.builder().build();

        model.addAttribute("member", member);
        model.addAttribute("editForm", editForm);

        return "dashboard/mypage/mypage_dashboard";
    }

    /**
     * 예약 확인 및 리뷰 작성 화면 출력
     *
     * @param page    선택된 페이지
     * @param type    검색 타입
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/reservation")
    public String reservationView(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                                  @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                                  HttpServletRequest request,
                                  Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        int selectPage = Integer.parseInt(page);
        int count = reservationService.getCountResByMember(loginMember.getId(), type);

        PageParams pageParams = PageParams.builder()
                .elementSize(ELEMENT_SIZE)
                .pageSize(PAGE_SIZE)
                .requestPage(selectPage)
                .rowCount(count)
                .offset((ELEMENT_SIZE * (selectPage - 1)))
                .type(type)
                .build();
        Pagination pagination = new Pagination(pageParams);

        List<Map<String, Object>> reservationList = reservationService.getMemberResList(loginMember.getId(), pageParams);

        ReviewForm reviewForm = ReviewForm.builder().build();

        model.addAttribute("reviewForm", reviewForm);
        model.addAttribute("reservationList", reservationList);
        model.addAttribute("pagination", pagination);

        return "dashboard/mypage/reservations";
    }

    /**
     * 예약 취소 기능
     *
     * @param reservationId 예약 ID
     * @param model         모델 객체
     * @return 성공시 반환 값
     */
    @DeleteMapping("/reservation")
    @ResponseBody
    public Object cancelReservation(@RequestBody int reservationId, Model model) {
        reservationService.cancelReservation(reservationId);

        return "success";
    }

    /**
     * 리뷰 작성 기능
     *
     * @param reviewForm 리뷰 폼 객체
     * @param model      모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/reservation")
    @ResponseBody
    public Object reviewRegist(@RequestBody ReviewForm reviewForm,
                               HttpServletRequest request,
                               Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        Review review = Review.builder()
                .content(reviewForm.getContent())
                .rating(reviewForm.getRating())
                .reservationId(reviewForm.getReservationId())
                .memberId(loginMember.getId())
                .build();

        reviewService.register(review);

        return "success";
    }

    /**
     * 리뷰 확인 화면 출력
     *
     * @param page    선택된 페이지
     * @param type    검색 타입
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/review")
    public String reviewView(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                             @RequestParam(value = "type", required = false, defaultValue = "latest") String type,
                             HttpServletRequest request,
                             Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        int selectPage = Integer.parseInt(page);
        int count = reviewService.getCountReviewByMember(loginMember.getId());
        PageParams pageParams = PageParams.builder()
                .elementSize(ELEMENT_SIZE)
                .pageSize(PAGE_SIZE)
                .requestPage(selectPage)
                .rowCount(count)
                .offset((ELEMENT_SIZE * (selectPage - 1)))
                .type(type)
                .build();
        Pagination pagination = new Pagination(pageParams);

        List<Map<String, Object>> reviewList = reviewService.getReviewListByMember(loginMember.getId(), pageParams);

        ReviewForm reviewForm = ReviewForm.builder().build();

        model.addAttribute("reviewForm", reviewForm);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("pagination", pagination);

        return "dashboard/mypage/reviews";
    }

    /**
     * 리뷰 수정 기능
     *
     * @param reviewForm 리뷰 폼 객체
     * @param model      모델 객체
     * @return 실행 후 반환 값
     */
    @PatchMapping("/review")
    @ResponseBody
    public Object reviewEdit(@RequestBody ReviewForm reviewForm, Model model) {
        Review review = Review.builder()
                .id(reviewForm.getReviewId())
                .content(reviewForm.getContent())
                .rating(reviewForm.getRating())
                .build();

        reviewService.editReview(review);

        return "success";
    }

    /**
     * 리뷰 삭제 기능
     *
     * @param reviewId 리뷰 ID
     * @param model    모델 객체
     * @return 실행 후 반환 값
     */
    @DeleteMapping("/review")
    @ResponseBody
    public Object removeEdit(@RequestBody int reviewId, Model model) {
        reviewService.removeReview(reviewId);

        return "success";
    }

}
