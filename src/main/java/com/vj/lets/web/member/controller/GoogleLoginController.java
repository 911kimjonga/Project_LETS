package com.vj.lets.web.member.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.vj.lets.domain.member.dto.Member;
import com.vj.lets.domain.member.service.GoogleLoginService;
import com.vj.lets.domain.member.service.MemberService;
import com.vj.lets.domain.member.util.DefaultPassword;
import com.vj.lets.domain.member.util.MemberType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleLoginService googleLoginService;
    private final MemberService memberService;

    @GetMapping("/google/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        googleLoginService.login(request, response);
    }

    @GetMapping("/member/callback")
    public RedirectView handleCallback(@RequestParam("code") String code, HttpServletRequest request) throws Exception {
        GoogleTokenResponse tokenResponse = googleLoginService.getToken(code);
        String accessToken = tokenResponse.getAccessToken();
        Person profile = googleLoginService.getUserInfo(accessToken);
        List<EmailAddress> emailAddresses = profile.getEmailAddresses();
        List<Name> names = profile.getNames();

        String userEmail = null;
        String userName = null;
        if(emailAddresses != null && !emailAddresses.isEmpty()) {
            userEmail = emailAddresses.get(0).getValue();
        }
        if(names != null && !names.isEmpty()) {
            userName = names.get(0).getDisplayNameLastFirst();
        }

        RedirectView redirectView = new RedirectView();
        HttpSession session = request.getSession();

        if (memberService.isMemberByEmail(userEmail) == null) {
            Member member = Member.builder()
                    .email(userEmail)
                    .name(userName)
                    .password(DefaultPassword.DEFAULT.getPassword())
                    .type(MemberType.GUEST.getType())
                    .build();

            memberService.register(member);
        }

        Member googleMember = memberService.isMemberByEmail(userEmail);
        session.setAttribute("googleMember", googleMember);

        redirectView.setUrl("/member/google");

        return redirectView;

    }

}
