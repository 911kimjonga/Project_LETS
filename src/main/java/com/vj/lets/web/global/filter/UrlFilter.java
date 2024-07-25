package com.vj.lets.web.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 잘못된 URL에 접근할 시 요청 오류 처리 필터 클래스
 * 
 * @author 김종원
 * @version 1.0
 * @since 24. 7. 25. (목)
 */
public class UrlFilter implements Filter {

    /**
     * 요청 가능한 URL 패턴
     */
    private final Pattern urlPattren = Pattern.compile("[^a-zA-Zㄱ-ㅎ가-힣0-9/?=&]") ;

    /**
     * 잘못된 URL에 접근할 시 요청 오류 처리 필터 구현 메소드
     * 
     * @param servletRequest 리퀘스트 객체
     * @param servletResponse 리스폰스 객체
     * @param filterChain 필터 체인
     * @throws IOException IOException
     * @throws ServletException ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String url = httpServletRequest.getRequestURI();
        String query = httpServletRequest.getQueryString();

        Matcher urlMatcher = urlPattren.matcher(url);

        if (query != null) {
            String decodeQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);
            Matcher queryMatcher = urlPattren.matcher(decodeQuery);
            if (urlMatcher.find() || queryMatcher.find()) {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } else {
            if (urlMatcher.find()) {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
