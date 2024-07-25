package com.vj.lets.web.global.resource;

import com.vj.lets.web.global.interceptor.AdminCheckInterceptor;
import com.vj.lets.web.global.interceptor.HostCheckInterceptor;
import com.vj.lets.web.global.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 인터셉터 등록 설정 Configuration
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-08 (금)
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    public final List<String> loginEssential = Arrays.asList("/**");

    public final List<String> loginNotEssential = Arrays.asList("/", "/**/*.ttf", "/**/*.woff", "/**/*.mp4", "/**/*.png", "/**/*.jpg", "/**/*.ico", "/**/*.html",
            "/**/assets/**", "/**/css/**", "/**/images/**", "/**/image/**", "/**/img/**", "/**/js/**", "/**/sass/**", "/**/upload_image/**", "/**/vendor/**",
            "/member/register", "/member/login", "/admin/login", "/host/login", "/member/google", "/member/callback", "/google/login", "/member/naver", "/cafe", "/cafe/*", "/group", "/contact", "/support/**", "/error",
            "/admin/**", "/host/**");

    public final List<String> loginAdminEssential = Arrays.asList("/admin/**");

    public final List<String> adminNotEssential = Arrays.asList("/", "/**/*.ttf", "/**/*.woff", "/**/*.mp4", "/**/*.png", "/**/*.jpg", "/**/*.ico", "/**/*.html",
            "/**/assets/**", "/**/css/**", "/**/images/**", "/**/image/**", "/**/img/**", "/**/js/**", "/**/sass/**", "/**/upload_image/**", "/**/vendor/**",
            "/admin/login", "/host/login");

    public final List<String> loginHostEssential = Arrays.asList("/host/**");

    public final List<String> hostNotEssential = Arrays.asList("/", "/**/*.ttf", "/**/*.woff", "/**/*.mp4", "/**/*.png", "/**/*.jpg", "/**/*.ico", "/**/*.html",
            "/**/assets/**", "/**/css/**", "/**/images/**", "/**/image/**", "/**/img/**", "/**/js/**", "/**/sass/**", "/**/upload_image/**", "/**/vendor/**",
            "/admin/login", "/host/login");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로그인 체크 인터셉터 등록
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns(loginEssential)
                .excludePathPatterns(loginNotEssential);

        registry.addInterceptor(new AdminCheckInterceptor())
                .order(2)
                .addPathPatterns(loginAdminEssential)
                .excludePathPatterns(adminNotEssential);

        registry.addInterceptor(new HostCheckInterceptor())
                .order(3)
                .addPathPatterns(loginHostEssential)
                .excludePathPatterns(hostNotEssential);
    }

}





