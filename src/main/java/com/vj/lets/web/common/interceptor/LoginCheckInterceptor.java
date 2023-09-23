package com.vj.lets.web.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 화면 요청 시 로그인 체크 인터셉터 구현
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-22 (금)
 */
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("loginMember") == null) {
            response.sendRedirect("/member/login");
            return false;
        }

        return true;
    }
}
