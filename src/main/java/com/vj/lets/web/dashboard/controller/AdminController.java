package com.vj.lets.web.dashboard.controller;

import com.vj.lets.domain.cafe.dto.Cafe;
import com.vj.lets.domain.cafe.service.CafeService;
import com.vj.lets.domain.common.web.PageParams;
import com.vj.lets.domain.common.web.Pagination;
import com.vj.lets.domain.member.dto.LoginForm;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.member.service.MemberService;
import com.vj.lets.domain.member.util.DefaultPassword;
import com.vj.lets.domain.member.util.MemberType;
import com.vj.lets.domain.support.dto.*;
import com.vj.lets.domain.support.service.ContactService;
import com.vj.lets.domain.support.service.FaqService;
import com.vj.lets.domain.support.util.ContactStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 관리자 대시보드 관련 요청 컨트롤러
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-10 (일)
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final ContactService contactService;
    private final CafeService cafeService;
    private final FaqService faqService;

    private static final int ELEMENT_SIZE = 3;
    private static final int PAGE_SIZE = 3;

    /**
     * 관리자 전용 로그인 화면 출력
     *
     * @param rememberEmail 쿠키에 저장된 이메일
     * @param model         모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/login")
    public String adminLoginView(@CookieValue(value = "remember", required = false) String rememberEmail,
                                 Model model) {
        LoginForm loginForm = LoginForm.builder().build();

        if (rememberEmail != null) {
            loginForm.setEmail(rememberEmail);
            loginForm.setRemember(true);
        }

        model.addAttribute("loginForm", loginForm);

        return "common/member/adminLogin";
    }

    /**
     * 관리자 대시보드 메인 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("")
    public String adminMain(Model model) {
        List<Map<String, Object>> countMember = memberService.getCountByRegMonth();
        int newMember = memberService.getCountByLastMonth();
        int newContact = contactService.getCountContact(ContactStatus.HOLD.getStatus());
        int newCafe = cafeService.getCountByLastMonth();

        model.addAttribute("newMember", newMember);
        model.addAttribute("newContact", newContact);
        model.addAttribute("newCafe", newCafe);
        model.addAttribute("countMember", countMember);

        return "dashboard/admin/admin_dashboard";
    }

    /**
     * 입점 신청 목록 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/contact")
    public String contactView(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                              @RequestParam(value = "type", required = false, defaultValue = "hold") String type,
                              Model model) {
        int selectPage = Integer.parseInt(page);
        int count = contactService.getCountContact(type);
        PageParams pageParams = PageParams.builder()
                .elementSize(ELEMENT_SIZE)
                .pageSize(PAGE_SIZE)
                .requestPage(selectPage)
                .rowCount(count)
                .type(type)
                .build();
        Pagination pagination = new Pagination(pageParams);

        List<Contact> contactList = contactService.getContactList(pageParams);

        ContactForm contactForm = ContactForm.builder().build();

        model.addAttribute("contactList", contactList);
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("pagination", pagination);

        return "dashboard/admin/contacts";
    }

    /**
     * 입점 신청 승인 기능
     *
     * @param id    입점 신청 ID
     * @param model 모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/contact")
    @ResponseBody
    public String contactApprove(@RequestBody int id, Model model) {
        Contact contact = contactService.getContact(id);

        Member member = Member.builder()
                .email(contact.getEmail())
                .name(contact.getName())
                .password(DefaultPassword.DEFAULT.getPassword())
                .type(MemberType.HOST.getType())
                .build();
        Cafe cafe = Cafe.builder()
                .email(contact.getEmail())
                .name(contact.getCafeName())
                .businessNumber(contact.getBusinessNumber())
                .build();

        contactService.approveContact(id, member, cafe);

        return "success";
    }

    /**
     * 입점 신청 거부 기능
     *
     * @param id    입점 신청 ID
     * @param model 모델 객체
     * @return 실행 후 반환 값
     */
    @DeleteMapping("/contact")
    @ResponseBody
    public String contactRefuse(@RequestBody int id, Model model) {
        contactService.refuseContact(id);

        return "success";
    }

    /**
     * FAQ 신규 등록 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/faq/register")
    public String faqRegisterView(Model model) {
        FaqForm faqForm = FaqForm.builder().build();
        List<FaqCategory> categoryList = faqService.getFaqCategoryList();

        model.addAttribute("faqForm", faqForm);
        model.addAttribute("categoryList", categoryList);

        return "dashboard/admin/faq_register";
    }

    /**
     * FAQ 신규 등록 기능
     *
     * @param faqForm FAQ 등록 폼 객체
     * @param model   모델 객체
     * @return 논리적 뷰 이름
     */
    @PostMapping("/faq/register")
    public String faqRegister(@ModelAttribute FaqForm faqForm, Model model) {
        Faq faq = Faq.builder()
                .title(faqForm.getTitle())
                .content(faqForm.getContent())
                .categoryId(faqForm.getCategory())
                .build();

        faqService.register(faq);

        return "redirect:/admin/faq";
    }

    /**
     * FAQ 목록 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/faq")
    public String faqListView(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                              @RequestParam(value = "type", required = false, defaultValue = "all") String type,
                              Model model) {
        List<FaqCategory> categoryList = faqService.getFaqCategoryList();

        int selectPage = Integer.parseInt(page);
        int count = faqService.getCountFaq(type);
        PageParams pageParams = PageParams.builder()
                .elementSize(ELEMENT_SIZE)
                .pageSize(PAGE_SIZE)
                .requestPage(selectPage)
                .rowCount(count)
                .type(type)
                .build();
        Pagination pagination = new Pagination(pageParams);

        List<Map<String, Object>> faqList = faqService.getFaqListForAdmin(pageParams);

        FaqForm faqForm = FaqForm.builder().build();

        model.addAttribute("faqForm", faqForm);
        model.addAttribute("faqList", faqList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("pagination", pagination);


        return "dashboard/admin/faqs";
    }

    /**
     * FAQ 수정 기능
     *
     * @param faqForm FAQ 폼 객체
     * @param model   모델 객체
     * @return 실행 후 반환 값
     */
    @PatchMapping("/faq")
    @ResponseBody
    public String faqEdit(@RequestBody FaqForm faqForm, Model model) {
        Faq faq = Faq.builder()
                .id(faqForm.getFaqId())
                .title(faqForm.getTitle())
                .content(faqForm.getContent())
                .categoryId(faqForm.getCategory())
                .build();

        faqService.edit(faq);

        return "success";
    }

    /**
     * FAQ 삭제 기능
     *
     * @param faqId FAQ ID
     * @param model 모델 객체
     * @return 실행 후 반환 값
     */
    @DeleteMapping("/faq")
    @ResponseBody
    public String faqRemove(@RequestBody int faqId, Model model) {
        faqService.remove(faqId);

        return "success";
    }

    /**
     * 차트 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/chart")
    public String chartView(Model model) {
        List<Map<String, Object>> countMember = memberService.getCountByRegMonth();
        List<Map<String, Object>> countCafe = cafeService.getCountByRegMonth();
        List<Map<String, Object>> countGender = memberService.getCountByGender();

        model.addAttribute("countMember", countMember);
        model.addAttribute("countCafe", countCafe);
        model.addAttribute("countGender", countGender);

        return "dashboard/admin/charts";
    }

    /**
     * 현재 입점 목록 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/host")
    public String hostView(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                           @RequestParam(value = "type", required = false, defaultValue = "latest") String type,
                           Model model) {
        int selectPage = Integer.parseInt(page);
        int count = cafeService.getCountCafeForAdmin();
        PageParams pageParams = PageParams.builder()
                .elementSize(ELEMENT_SIZE)
                .pageSize(PAGE_SIZE)
                .requestPage(selectPage)
                .rowCount(count)
                .type(type)
                .build();
        Pagination pagination = new Pagination(pageParams);

        List<Map<String, Object>> cafeList = cafeService.getCafeListForAdmin(pageParams);

        model.addAttribute("cafeList", cafeList);
        model.addAttribute("pagination", pagination);

        return "dashboard/admin/hosts";
    }

    /**
     * 현재 회원 목록 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/member")
    public String memberView(Model model) {
        List<Member> memberList = memberService.getMemberList();

        model.addAttribute("memberList", memberList);

        return "dashboard/admin/members";
    }

}
