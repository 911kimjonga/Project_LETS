package com.vj.lets.web.dashboard.controller;

import com.vj.lets.domain.cafe.dto.Cafe;
import com.vj.lets.domain.cafe.dto.CafeEditForm;
import com.vj.lets.domain.cafe.dto.CafeOption;
import com.vj.lets.domain.cafe.dto.OptionListForm;
import com.vj.lets.domain.cafe.service.CafeService;
import com.vj.lets.domain.common.web.PageParams;
import com.vj.lets.domain.common.web.Pagination;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.reservation.dto.Reservation;
import com.vj.lets.domain.reservation.service.ReservationService;
import com.vj.lets.domain.review.dto.Review;
import com.vj.lets.domain.review.dto.ReviewForm;
import com.vj.lets.domain.review.service.ReviewService;
import com.vj.lets.domain.room.dto.Room;
import com.vj.lets.domain.room.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 호스트 대시보드 관련 요청 컨트롤러
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-10 (일)
 */
@Controller
@RequestMapping("/host")
@RequiredArgsConstructor
public class HostController {

    private final CafeService cafeService;
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final ReviewService reviewService;

    private static final int ELEMENT_SIZE = 5;
    private static final int PAGE_SIZE = 5;

    /**
     * 실제 회원 이미지 경로
     */
    @Value("${cafe.imageLocation}")
    private String imageLocationCafe;

    /**
     * DB에 입력할 회원 이미지 경로
     */
    @Value("${cafe.imageDBPath}")
    private String imageDBPathCafe;

    /**
     * 실제 회원 이미지 경로
     */
    @Value("${room.imageLocation}")
    private String imageLocationRoom;

    /**
     * DB에 입력할 회원 이미지 경로
     */
    @Value("${room.imageDBPath}")
    private String imageDBPathRoom;

    /**
     * 호스트 대시보드 메인 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("")
    public String hostMain(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        Map<String, Object> cafe = cafeService.getCafeMemberId(loginMember.getId());
        int cafeId = Integer.parseInt(cafe.get("id").toString());
        List<Map<String, Object>> countRes = reservationService.getCountByResMonth(cafeId);
        List<Map<String, Object>> monthlySales = reservationService.getMonthlySales(cafeId);
        int reviewCount = reviewService.getTodayReview(cafeId);
        int resCount = reservationService.getTotalRes(cafeId);

        model.addAttribute("countRes", countRes);
        model.addAttribute("monthlySales", monthlySales);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("resCount", resCount);

        return "dashboard/host/host_dashboard";
    }

    /**
     * 카페 정보 조회
     *
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     */
    @GetMapping("/cafe")
    public String cafeRegister(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            Map<String, Object> cafe = cafeService.getCafeMemberId(loginMember.getId());
            model.addAttribute("cafe", cafe);
            int cafeId = Integer.parseInt(cafe.get("id").toString());
            List<CafeOption> cafeOptions = cafeService.getCafeOptionCafeId(cafeId);
            model.addAttribute("cafeOptions", cafeOptions);

            List<CafeOption> allOption = cafeService.getOptionList();

            List<OptionListForm> optionListForms = new ArrayList<>();
            for (CafeOption option : allOption) {
                OptionListForm optionListForm = OptionListForm.builder()
                        .optionId(option.getId())
                        .optionName(option.getName())
                        .imagePath(option.getImagePath())
                        .optionCheck(cafeService.cafeOptionCheck(cafeId, option.getId()))
                        .build();
                optionListForms.add(optionListForm);
            }
            model.addAttribute("optionListForms", optionListForms);
        }

        return "dashboard/host/cafe_register";
    }

    /**
     * 카페 정보 수정
     *
     * @param cafeEditForm 카페 수정 폼
     * @param imagePath    화면 이미지
     * @param request      서블릿 리퀘스트 객체
     * @param model        모델 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     */
    @PostMapping("/cafe/edit")
    public String cafeUpdate(@ModelAttribute CafeEditForm cafeEditForm,
                             MultipartFile imagePath,
                             HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            Map<String, Object> cafes = cafeService.getCafeMemberId(loginMember.getId());

            int cafeId = Integer.parseInt(cafes.get("id").toString());
            Cafe cafeRe = Cafe.builder()
                    .id(cafeId)
                    .businessNumber(cafeEditForm.getBusinessNumber())
                    .name(cafeEditForm.getName())
                    .phoneNumber(cafeEditForm.getPhoneNumber())
                    .roadAddress(cafeEditForm.getRoadAddress())
                    .detailAddress(cafeEditForm.getDetailAddress())
                    .latitude(cafeEditForm.getLatitude())
                    .longitude(cafeEditForm.getLongitude())
                    .startTime(cafeEditForm.getStartTime())
                    .endTime(cafeEditForm.getEndTime())
                    .description(cafeEditForm.getDescription())
                    .build();

            if (!imagePath.isEmpty()) {
                // 이미지 폴더에 저장
                // 업로드 이미지 확장자 가져오기
                String imageExtension = StringUtils.getFilenameExtension(imagePath.getOriginalFilename());
                // 업로드 한 이미지 다운로드 받을 위치 설정
                StringBuilder imageDir = new StringBuilder();
                imageDir.append(imageLocationCafe).append(cafeId).append(".").append(imageExtension);
                File uploadDir = new File(imageDir.toString());
                // 폴더 없으면 생성
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                try {
                    imagePath.transferTo(uploadDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                StringBuilder imagePathDB = new StringBuilder();
                imagePathDB.append(imageDBPathCafe).append(cafeId).append(".").append(imageExtension);
                cafeRe.setImagePath(imagePathDB.toString());
            }

            String comment = "host";
            String siGunGu = cafeEditForm.getSiGunGuName();
            String siDo = cafeEditForm.getSiDoName();

            cafeService.editCafe(cafeId, siGunGu, siDo, cafeRe, comment, cafeEditForm.getOptions());
        }

        return "redirect:/host/cafe";
    }

    /**
     * 룸 리스트 조회
     *
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     */
    @GetMapping("/room")
    public String roomList(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            Map<String, Object> cafe = cafeService.getCafeMemberId(loginMember.getId());
            int cafeId = Integer.parseInt(cafe.get("id").toString());
            List<Room> rooms = roomService.getSearchCafeRoom(cafeId);

            model.addAttribute("rooms", rooms);
        }

        return "dashboard/host/room_table";
    }

    /**
     * 룸 상세정보 조회
     *
     * @param id    룸 ID
     * @param model 모델 객체
     * @return 논리적 뷰이름
     * @author VJ특공대 강소영
     */
    @GetMapping("/room/{id}")
    public String roomDetail(@PathVariable int id, Model model) {
        Room room = roomService.getSearchRoom(id);
        Room roomForm = Room.builder().build();

        model.addAttribute("room", room);
        model.addAttribute("roomForm", roomForm);

        return "dashboard/host/room_register";
    }

    /**
     * 룸 정보 수정
     *
     * @param id        룸 ID
     * @param imagePath 화면 이미지
     * @param roomForm  룸 변경 정보
     * @param model     모델 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     */
    @PostMapping("/room/{id}/edit")
    public String roomUpdate(@PathVariable String id,
                             MultipartFile imagePath,
                             @ModelAttribute Room roomForm,
                             Model model) {
        Room editRoom = Room.builder()
                .id(Integer.parseInt(id))
                .name(roomForm.getName())
                .headCount(roomForm.getHeadCount())
                .price(roomForm.getPrice())
                .description(roomForm.getDescription())
                .build();

        if (!imagePath.isEmpty()) {
            // 이미지 폴더에 저장
            // 업로드 이미지 확장자 가져오기
            String imageExtension = StringUtils.getFilenameExtension(imagePath.getOriginalFilename());
            // 업로드 한 이미지 다운로드 받을 위치 설정
            StringBuilder imageDir = new StringBuilder();
            imageDir.append(imageLocationRoom).append(id).append(".").append(imageExtension);
            File uploadDir = new File(imageDir.toString());
            // 폴더 없으면 생성
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            try {
                imagePath.transferTo(uploadDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            StringBuilder imagePathDB = new StringBuilder();
            imagePathDB.append(imageDBPathRoom).append(id).append(".").append(imageExtension);
            editRoom.setImagePath(imagePathDB.toString());
        }

        roomService.editRoom(editRoom);

        return "redirect:/host/room/{id}";
    }

    /**
     * 새로운 룸 등록
     *
     * @param roomRegist 룸 등록 객체
     * @param request    서블릿 리퀘스트 객체
     * @param model      모댈 객체
     * @return 논리적 뷰 이름
     * @author VJ특공대 강소영
     */
    @PostMapping("/room/regist")
    public String roomRegist(@ModelAttribute Room roomRegist,
                             HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            Map<String, Object> cafe = cafeService.getCafeMemberId(loginMember.getId());
            int cafeId = Integer.parseInt(cafe.get("id").toString());
            Room roomNew = Room.builder()
                    .name(roomRegist.getName())
                    .headCount(roomRegist.getHeadCount())
                    .price(roomRegist.getPrice())
                    .description(roomRegist.getDescription())
                    .cafeId(cafeId)
                    .build();

            roomService.register(roomNew);
        }

        return "redirect:/host/room";
    }

    /**
     * 호스트 카페의 예약 리스트 페이지
     *
     * @param page    페이지
     * @param type    페이지 요청 타입
     * @param request 리퀘스트
     * @param model   모델 객
     * @return 호스트의 카페의 예약 리스트
     */
    @GetMapping("/bookings")
    public String hostBookingList(@RequestParam(value = "page", required = false) String page,
                                  @RequestParam(value = "type", required = false) String type,
                                  HttpServletRequest request,
                                  Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        Map<String, Object> cafe = cafeService.getCafeMemberId(loginMember.getId());
        int cafeId = Integer.parseInt(cafe.get("id").toString());
        if (page == null || page.isBlank()) {
            page = "1";
        }
        if (type == null || type.isBlank()) {
            type = "all";
        }
        int selectPage = Integer.parseInt(page);
        int count = reservationService.getCountResByHost(cafeId, type);
        PageParams pageParams = PageParams.builder()
                .elementSize(ELEMENT_SIZE)
                .pageSize(PAGE_SIZE)
                .requestPage(selectPage)
                .rowCount(count)
                .type(type)
                .build();

        Pagination pagination = new Pagination(pageParams);

        List<Map<String, Object>> hostReservationList = reservationService.getHostResList(cafeId, pageParams);

        model.addAttribute("hostReservationList", hostReservationList);
        model.addAttribute("pagination", pagination);

        return "dashboard/host/bookings";
    }


    /**
     * 호스트 카페에 등록된 체뷰 출력
     *
     * @param page    페이지
     * @param type    페이지 요청 타입
     * @param request 리퀘스트
     * @param model   모델 객체
     * @return 호스트 카페에 등록된 리뷰 출력 (guest의 리뷰만 출력됨)
     */
    @GetMapping("/reviews")
    public String hostreviewList(@RequestParam(value = "page", required = false) String page,
                                 @RequestParam(value = "type", required = false) String type,
                                 HttpServletRequest request,
                                 Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        Map<String, Object> cafe = cafeService.getCafeMemberId(loginMember.getId());
        int cafeId = Integer.parseInt(cafe.get("id").toString());
        if (page == null || page.isBlank()) {
            page = "1";
        }
        if (type == null || type.isBlank()) {
            type = "latest";
        }
        int selectPage = Integer.parseInt(page);
        int count = reviewService.getCountByHost(cafeId);
        PageParams pageParams = PageParams.builder()
                .elementSize(ELEMENT_SIZE)
                .pageSize(PAGE_SIZE)
                .requestPage(selectPage)
                .rowCount(count)
                .type(type)
                .build();
        Pagination pagination = new Pagination(pageParams);
        List<Map<String, Object>> hostReviewList = reviewService.getByHost(cafeId, pageParams);
        ReviewForm reviewForm = ReviewForm.builder().build();

        model.addAttribute("reviewForm", reviewForm);
        model.addAttribute("hostReviewList", hostReviewList);
        model.addAttribute("pagination", pagination);

        return "dashboard/host/reviews";
    }


    /**
     * 리뷰 답변 작성 기능
     *
     * @param reviewForm 리뷰 폼 객체
     * @param model      모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/reviews")
    @ResponseBody
    public Object reviewRegist(@SessionAttribute Member loginMember, @RequestBody ReviewForm reviewForm, Model model) {
        Review review = Review.builder()
                .content(reviewForm.getContent())
                .reservationId(reviewForm.getReservationId())
                .memberId(loginMember.getId())
                .build();

        reviewService.register(review);

        return "success";
    }

    /**
     * 리뷰 수정 기능
     *
     * @param reviewForm 리뷰 폼 객체
     * @param model      모델 객체
     * @return 실행 후 반환 값
     */
    @PatchMapping("/reviews")
    @ResponseBody
    public Object reviewEdit(@RequestBody ReviewForm reviewForm, Model model) {
        Review review = Review.builder()
                .id(reviewForm.getReviewId())
                .content(reviewForm.getContent())
                .build();

        reviewService.editReview(review);

        return "success";
    }

    /**
     * 호스트 카페의 예약에 대한 종합 데이터 출력
     *
     * @param request 리퀘스트
     * @param model   모델 객체
     * @return 호스트 카페의 예약에 대한 종합 데이터 (예약 취소 제외)
     */
    @GetMapping("/stats")
    public String hostTotalData(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            Map<String, Object> cafe = cafeService.getCafeMemberId(loginMember.getId());
            int cafeId = Integer.parseInt(cafe.get("id").toString());
            List<Map<String, Reservation>> reserveList = reservationService.getTotalData(cafeId);
            model.addAttribute("reserveList", reserveList);
        }

        return "dashboard/host/tables";
    }
}
