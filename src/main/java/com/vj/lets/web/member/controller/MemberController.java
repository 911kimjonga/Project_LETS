package com.vj.lets.web.member.controller;

import com.vj.lets.domain.member.dto.*;
import com.vj.lets.domain.member.service.MemberService;
import com.vj.lets.domain.member.util.DefaultPassword;
import com.vj.lets.domain.member.util.MemberCrypt;
import com.vj.lets.domain.member.util.MemberType;
import com.vj.lets.web.global.infra.S3FileUpload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 회원 관련 요청 컨트롤러
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final S3FileUpload s3FileUpload;

    /**
     * 회원 가입 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/register")
    public String registerView(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            return "index";
        }

        return "common/member/register";
    }

    /**
     * 회원 가입 기능
     *
     * @param memberVO  회원가입 정보
     * @param bindingResult 바인딩 리절트 객체
     * @param model         모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/register")
    @ResponseBody
    public Object register(@RequestBody MemberVO memberVO,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "fail";
        }

        if (memberService.isMemberByEmail(memberVO.getEmail()) != null) {
            return "duplicate";
        }

        Member member = Member.builder()
                .email(memberVO.getEmail())
                .name(memberVO.getName())
                .password(memberService.encodeBcrypt(memberVO.getPassword(), MemberCrypt.STRENGTH.getStrength()))
                .type(MemberType.GUEST.getType())
                .build();

        memberService.register(member);

        return "success";
    }

    /**
     * 회원 로그인 화면 출력
     *
     * @param rememberEmail 쿠키에 저장된 이메일
     * @param model         모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/login")
    public String loginView(@CookieValue(value = "remember", required = false) String rememberEmail,
                            HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember != null) {
            return "redirect:/";
        }

        if (rememberEmail != null) {
            model.addAttribute("rememberEmail", rememberEmail);
            model.addAttribute("remember", true);
        } else {
            model.addAttribute("rememberEmail", "guest1@gmail.com");
        }

        return "common/member/login";
    }

    /**
     * 회원 로그인 기능
     *
     * @param memberVO      로그인 정보
     * @param bindingResult 바인딩 리절트 객체
     * @param request       서블릿 리퀘스트 객체
     * @param model         모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody MemberVO memberVO,
                        BindingResult bindingResult,
                        HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        if (session != null) {
            session.invalidate();
            session = request.getSession();
        }

        if (bindingResult.hasErrors()) {
            return "fail";
        }

        Member cryptMember = memberService.isMemberByEmail(memberVO.getEmail());
        Boolean cryptMatch = memberService.matchesBcrypt(memberVO.getPassword(), cryptMember.getPassword(), MemberCrypt.STRENGTH.getStrength());

        if (cryptMatch) {
            Member loginMember = Member.builder()
                    .id(cryptMember.getId())
                    .email(cryptMember.getEmail())
                    .name(cryptMember.getName())
                    .type(cryptMember.getType())
                    .imagePath(cryptMember.getImagePath())
                    .build();

            if (loginMember == null) {
                bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
                return "fail";
            }

            if (loginMember.getType().equals(MemberType.GUEST.getType())) {
                session.setAttribute("loginMember", loginMember);
                return "success";
            } else {
                return "fail";
            }

        } else {
            return "fail";
        }

    }

    /**
     * 구글 로그인 처리 기능
     *
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/google")
    public String googleLogin(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("googleMember");

        session.setAttribute("loginMember", loginMember);

        return "redirect:/";
    }

    /**
     * 네이버 로그인 처리 기능
     *
     * @param email   네이버 이메일
     * @param name    네이버 이름
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/naver")
    @ResponseBody
    public String naverLogin(@RequestParam String email, @RequestParam String name,
                             HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        if (memberService.isMemberByEmail(email) == null) {
            Member member = Member.builder()
                    .email(email)
                    .name(name)
                    .password(DefaultPassword.DEFAULT.getPassword())
                    .type(MemberType.GUEST.getType())
                    .build();

            memberService.register(member);
        }

        Member naverMember = memberService.isMemberByEmail(email);

        session.setAttribute("loginMember", naverMember);

        return "success";
    }

    /**
     * 회원 정보 수정 기능
     *
     * @param memberVO  회원 정보 수정 정보
     * @param imagePath 회원 이미지
     * @param request   서블릿 리퀘스트 객체
     * @param model     모델 객체
     * @return 논리적 뷰 이름
     */
    @PostMapping("/edit")
    @ResponseBody
    public String edit(@RequestPart("editData") MemberVO memberVO,
                       @RequestPart(value = "imagePath", required = false)  MultipartFile imagePath,
                       HttpServletRequest request, Model model) throws IOException {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        Member checkMember = memberService.checkEdit(loginMember.getId());

        if (!memberVO.equals(checkMember) || imagePath != null) {
            // DB에 수정 정보 입력
            Member editMember = Member.builder()
                    .id(loginMember.getId())
                    .password(memberService.encodeBcrypt(memberVO.getPassword(), MemberCrypt.STRENGTH.getStrength()))
                    .name(memberVO.getName())
                    .gender(memberVO.getGender())
                    .birthday(memberVO.getBirthday())
                    .phoneNumber(memberVO.getPhoneNumber())
                    .build();

            if (imagePath != null) {
                String objectUrl = s3FileUpload.imageUpload(imagePath, this, editMember.getId());
                editMember.setImagePath(objectUrl);
            }

            memberService.editMember(editMember);

            return "success";

        } else {
            return "fail";
        }

    }

    /**
     * 회원 탈퇴
     *
     * @param password 회원 비밀번호
     * @param request  서블릿 리퀘스트 객체
     * @param model    모델 객체
     * @return 실행 후 반환 값
     */
    @DeleteMapping("/delete")
    @ResponseBody
    public String delete(@RequestBody String password, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (memberService.isMember(loginMember.getEmail(), password) != null) {
            memberService.removeMember(loginMember.getId());

            if (session != null) {
                session.invalidate();
            }

            return "success";

        } else {
            return "fail";
        }
    }

    /**
     * 회원 로그아웃 기능
     *
     * @param session 세션 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";

    }

}
