package com.vj.lets.web.member.controller;

import com.vj.lets.domain.member.dto.EditForm;
import com.vj.lets.domain.member.dto.LoginForm;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.member.dto.RegisterForm;
import com.vj.lets.domain.member.service.MemberService;
import com.vj.lets.domain.member.util.DefaultPassword;
import com.vj.lets.domain.member.util.MemberType;
import com.vj.lets.web.global.infra.S3FileUpload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;

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

        RegisterForm registerForm = RegisterForm.builder().build();

        model.addAttribute("registerForm", registerForm);

        return "common/member/register";
    }

    /**
     * 회원 가입 기능
     *
     * @param registerForm  회원가입 폼 객체
     * @param bindingResult 바인딩 리절트 객체
     * @param model         모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/register")
    @ResponseBody
    public Object register(@Validated @RequestBody RegisterForm registerForm,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "fail";
        }

        if (memberService.isMemberByEmail(registerForm.getEmail()) != null) {
            return "duplicate";
        }

        Member member = Member.builder()
                .email(registerForm.getEmail())
                .name(registerForm.getName())
                .password(registerForm.getPassword())
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
            return "index";
        }

        LoginForm loginForm = LoginForm.builder().build();

        if (rememberEmail != null) {
            loginForm.setEmail(rememberEmail);
            loginForm.setRemember(true);
        }

        model.addAttribute("loginForm", loginForm);

        return "common/member/login";
    }

    /**
     * 회원 로그인 기능
     *
     * @param loginForm     로그인 폼 객체
     * @param bindingResult 바인딩 리절트 객체
     * @param request       서블릿 리퀘스트 객체
     * @param model         모델 객체
     * @return 실행 후 반환 값
     */
    @PostMapping("/login")
    @ResponseBody
    public String login(@Validated @RequestBody LoginForm loginForm,
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

        Member loginMember = memberService.isMember(loginForm.getEmail(), loginForm.getPassword());
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
    public String naverLogin(@RequestParam String email, @RequestParam String name, HttpServletRequest request, Model model) {
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
     * @param editForm  회원 정보 수정 폼 객체
     * @param imagePath 회원 이미지
     * @param request   서블릿 리퀘스트 객체
     * @param response  서블릿 리스폰스 객체
     * @param model     모델 객체
     * @return 논리적 뷰 이름
     */
    @PostMapping("/edit")
    public String edit(@ModelAttribute EditForm editForm,
                       MultipartFile imagePath,
                       HttpServletRequest request, HttpServletResponse response,
                       Model model) throws IOException {
        HttpSession session = request.getSession();
        Member loginMember = (Member) session.getAttribute("loginMember");

        EditForm checkForm = memberService.checkEdit(loginMember.getId());

        if (!editForm.equals(checkForm) || !imagePath.isEmpty()) {
            // DB에 수정 정보 입력
            Member editMember = Member.builder()
                    .id(loginMember.getId())
                    .password(editForm.getPassword())
                    .name(editForm.getName())
                    .gender(editForm.getGender())
                    .birthday(editForm.getBirthday())
                    .phoneNumber(editForm.getPhoneNumber())
                    .build();

            if (!imagePath.isEmpty()) {
                String objectUrl = s3FileUpload.imageUpload(imagePath, this, editMember.getId());
                editMember.setImagePath(objectUrl);
            }

            memberService.editMember(editMember);

        } else {

            try {
                response.setContentType("text/html; charset=utf-8");
                PrintWriter w = response.getWriter();
                w.write("<script>alert('수정 정보가 기존 정보와 이미 일치합니다.');location.href='/';</script>");
                w.flush();
                w.close();
            } catch (Exception e) {
                throw new RuntimeException("오류 메세지");
            }
        }

        return "redirect:/mypage";
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
