package com.vj.lets.web.common.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 인터셉터 등록 자바 설정 클래스 구현
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @see LoginModalInterceptor
 * @since 2023-09-08 (금)
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    public final List<String> loginEssential = Arrays.asList("/**");

    public final List<String> loginNotEssential = Arrays.asList("/", "/**/*.ttf", "/**/*.woff", "/**/*.mp4", "/**/*.png", "/**/*.jpg", "/**/*.ico", "/**/*.html",
            "/**/assets/**", "/**/css/**", "/**/images/**", "/**/image/**", "/**/img/**", "/**/js/**", "/**/sass/**", "/**/upload_image/**", "/**/vendor/**",
            "/member/register", "/member/login", "/member/logout", "/member/google", "/member/callback", "/google/login", "/cafe", "/cafe/*", "/group", "/contact", "/support/**", "/error");

    public final List<String> loginModalNotEssential = Arrays.asList("/**/*.ttf", "/**/*.woff", "/**/*.mp4", "/**/*.png", "/**/*.jpg", "/**/*.ico", "/**/*.html",
            "/**/assets/**", "/**/css/**", "/**/images/**", "/**/image/**", "/**/img/**", "/**/js/**", "/**/sass/**", "/**/upload_image/**", "/**/vendor/**",
            "/mypage/**", "/host/**", "/admin/**", "/member/login/**", "/member/register/**");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로그인 체크 인터셉터 등록
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns(loginEssential)
                .excludePathPatterns(loginNotEssential);

        // 로그인 모달 창 인터셉터 등록
        registry.addInterceptor(new LoginModalInterceptor())
                .order(3)
                .addPathPatterns(loginEssential)
                .addPathPatterns(loginModalNotEssential);
    }

}





