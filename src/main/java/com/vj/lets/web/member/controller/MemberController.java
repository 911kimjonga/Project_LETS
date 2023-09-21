package com.vj.lets.web.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vj.lets.domain.member.dto.*;
import com.vj.lets.domain.member.service.MemberService;
import com.vj.lets.domain.member.util.MemberType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 회원 관련 요청 컨트롤러 (회원 가입, 회원 로그인, 회원 로그아웃)
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * 실제 회원 이미지 경로
     */
    @Value("${member.imageLocation}")
    private String imageLocation;

    /**
     * DB에 입력할 회원 이미지 경로
     */
    @Value("${member.imageDBPath}")
    private String imageDBPath;

    /**
     * 회원 가입 화면 출력
     *
     * @param model 모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/register")
    public String registerView(Model model) {

        RegisterForm registerForm = RegisterForm.builder().build();
        model.addAttribute("registerForm", registerForm);

        return "common/member/register";
    }

    /**
     * 회원 가입 기능
     *
     * @param registerForm  회원가입 폼 객체
     * @param bindingResult 바인딩 리절트 객체
     * @param response      서블릿 리스폰스 객체
     * @param model         모델 객체
     * @return 논리적 뷰 이름
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterForm registerForm,
                           BindingResult bindingResult,
                           HttpServletResponse response, Model model) {

        if (bindingResult.hasErrors()) {
            return "redirect:/member/register";
        }

        // 데이터 검증 처리 과정 추가 예정

        Member member = Member.builder()
                .email(registerForm.getEmail())
                .name(registerForm.getName())
                .password(registerForm.getPassword())
                .type(MemberType.GUEST.getType())
                .build();

        memberService.register(member);


        try {
            response.setContentType("text/html; charset=utf-8");
            PrintWriter w = response.getWriter();
            w.write("<script>alert('회원가입이 완료되었습니다.');location.href='/member/login';</script>");
            w.flush();
            w.close();
        } catch (Exception e) {
            throw new RuntimeException("오류 메세지");
        }

        return "redirect:/member/login";
    }

    /**
     * 로그인 화면 출력
     *
     * @param rememberEmail 쿠키에 저장된 이메일
     * @param model         모델 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/login")
    public String loginView(@CookieValue(value = "remember", required = false) String rememberEmail, Model model) {
        LoginForm loginForm = LoginForm.builder().build();

        if (rememberEmail != null) {
            loginForm.setEmail(rememberEmail);
            loginForm.setRemember(true);
        }

        model.addAttribute("loginForm", loginForm);

        return "common/member/login";
    }

    /**
     * 로그인 기능
     *
     * @param loginForm     로그인 폼 객체
     * @param bindingResult 리절트 객체
     * @param request       서블릿 리퀘스트 객체
     * @param response      서블릿 리스폰스 객체
     * @param model         모델 객체
     * @return 논리적 뷰 이름
     */
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm,
                        BindingResult bindingResult,
                        HttpServletRequest request, HttpServletResponse response, Model model) {

        if (bindingResult.hasErrors()) {
            return "redirect:/member/login";
        }

        // 데이터 검증 처리 과정 추가 예정

        Member loginMember = memberService.isMember(loginForm.getEmail(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");

            try {
                response.setContentType("text/html; charset=utf-8");
                PrintWriter w = response.getWriter();
                w.write("<script>alert('아이디 또는 비밀번호가 맞지 않습니다.');location.href='/member/login';</script>");
                w.flush();
                w.close();
            } catch (Exception e) {
                throw new RuntimeException("오류 메세지");
            }

            return "redirect:/member/login";
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginMember);

        // 쿠키 생성
        if (loginForm.getRemember() != null && loginForm.getRemember()) {
            Cookie saveCookie;
            saveCookie = new Cookie("remember", loginMember.getEmail());
            saveCookie.setPath("/");
            saveCookie.setMaxAge(60 * 60 * 24 * 100);
            response.addCookie(saveCookie);
        } else if (loginForm.getRemember() == null || !loginForm.getRemember()) {
            Cookie saveCookie;
            saveCookie = new Cookie("remember", loginMember.getEmail());
            saveCookie.setPath("/");
            saveCookie.setMaxAge(0);
            response.addCookie(saveCookie);
        }

        String redirectURI = (String) session.getAttribute("redirectURI");
        log.warn("======================{}", redirectURI);
        String uri = redirectURI == null ? "/" : redirectURI;
        return "redirect:" + uri;
    }

    /**
     * 회원 정보 수정 기능
     *
     * @param editForm      회원 정보 수정 폼 객체
     * @param bindingResult 바인딩 리절트 객체
     * @param request       서블릿 리퀘스트 객체
     * @param response      서블릿 리스폰스 객체
     * @param model         모델 객체
     * @return 논리적 뷰 이름
     */
    @PostMapping("/edit")
    public String edit(@Valid @ModelAttribute EditForm editForm, BindingResult bindingResult,
                       MultipartFile imagePath,
                       HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

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
                // 이미지 폴더에 저장
                // 업로드 이미지 확장자 가져오기
                String imageExtension = StringUtils.getFilenameExtension(imagePath.getOriginalFilename());
                // 업로드 한 이미지 다운로드 받을 위치 설정
                StringBuilder imageDir = new StringBuilder();
                imageDir.append(imageLocation).append(loginMember.getId()).append(".").append(imageExtension);
                File uploadDir = new File(imageDir.toString());
                // 폴더 없으면 생성
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                imagePath.transferTo(uploadDir);

                StringBuilder imagePathDB = new StringBuilder();
                imagePathDB.append(imageDBPath).append(loginMember.getId()).append(".").append(imageExtension);
                editMember.setImagePath(imagePathDB.toString());
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

        String redirectURI = (String) session.getAttribute("redirectURI");
        String uri = redirectURI == null ? "/" : redirectURI;

        return "redirect:" + uri;
    }

    /**
     * 회원 탈퇴
     *
     * @param request 서블릿 리퀘스트 객체
     * @param model   모델 객체
     * @return 성공시 반환 값
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
     * 로그아웃 기능
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
