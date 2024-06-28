package com.vj.lets.web.member.controller;

import com.vj.lets.domain.member.dto.EditForm;
import com.vj.lets.domain.member.dto.LoginForm;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.member.dto.RegisterForm;
import com.vj.lets.domain.member.service.MemberService;
import com.vj.lets.domain.member.util.MemberType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
                            Model model) {
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

        if (bindingResult.hasErrors()) {
            return "fail";
        }

        Member loginMember = memberService.isMember(loginForm.getEmail(), loginForm.getPassword());
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "fail";
        }

        session.setAttribute("loginMember", loginMember);

        return "success";
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
     * 로그아웃 기능
     *
     * @param session 세션 객체
     * @return 논리적 뷰 이름
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (session != null) {
            session.invalidate();
        }

        if (loginMember.getType().equals(MemberType.ADMIN.getType())) {
            return "redirect:/admin/login";
        } else {
            return "redirect:/";
        }

    }

}
