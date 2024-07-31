package com.vj.lets.web.global.resource;

import com.vj.lets.web.global.filter.UrlFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 필터 등록 설정 Configuration
 *
 * @author 김종원
 * @version 1.0
 * @since 24. 7. 25. (목)
 */
@Configuration
public class FilterConfig implements WebMvcConfigurer {

    /**
     * 필터 등록
     *
     * @return 필터 Bean 객체
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new UrlFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/");
        filterRegistrationBean.addUrlPatterns("/member/*");
        filterRegistrationBean.addUrlPatterns("/cafe/*");
        filterRegistrationBean.addUrlPatterns("/group/*");
        filterRegistrationBean.addUrlPatterns("/reservation/*");
        filterRegistrationBean.addUrlPatterns("/support/*");
        filterRegistrationBean.addUrlPatterns("/admin/*");
        filterRegistrationBean.addUrlPatterns("/host/*");
        filterRegistrationBean.addUrlPatterns("/mypage/*");
        filterRegistrationBean.addUrlPatterns("/google/*");
        filterRegistrationBean.addUrlPatterns("/login/*");

        return filterRegistrationBean;
    }

}
