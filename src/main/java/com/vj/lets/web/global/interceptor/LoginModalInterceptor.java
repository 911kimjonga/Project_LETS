package com.vj.lets.web.global.interceptor;

import com.vj.lets.domain.member.dto.LoginForm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 로그인 모달 인터셉터 구현
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@Component
public class LoginModalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String rememberEmail = null;
        if (session.getAttribute("loginMember") == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase("remember")) {
                        rememberEmail = cookie.getValue();
                    }
                }
            }
            LoginForm loginForm = LoginForm.builder().build();
            if (rememberEmail != null) {
                loginForm.setEmail(rememberEmail);
                loginForm.setRemember(true);
            }
            request.setAttribute("loginForm", loginForm);
        }
        return true;
    }

}

