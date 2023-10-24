package com.vj.lets.web.common.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 이미지 등록 및 로딩 자바 설정 클래스 구현
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-25 (월)
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${member.imageResourcePath}")
    private String memberResourcePath;

    @Value("${member.imageUploadPath}")
    private String memberUploadPath;

    @Value("${cafe.imageResourcePath}")
    private String cafeResourcePath;

    @Value("${cafe.imageUploadPath}")
    private String cafeUploadPath;

    @Value("${room.imageResourcePath}")
    private String roomResourcePath;

    @Value("${room.imageUploadPath}")
    private String roomUploadPath;

    @Value("${group.imageResourcePath}")
    private String groupResourcePath;

    @Value("${group.imageUploadPath}")
    private String groupUploadPath;

    @Value("${article.imageResourcePath}")
    private String articleResourcePath;

    @Value("${article.imageUploadPath}")
    private String articleUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(memberUploadPath).addResourceLocations(memberResourcePath);
        registry.addResourceHandler(cafeUploadPath).addResourceLocations(cafeResourcePath);
        registry.addResourceHandler(roomUploadPath).addResourceLocations(roomResourcePath);
        registry.addResourceHandler(groupUploadPath).addResourceLocations(groupResourcePath);
        registry.addResourceHandler(articleUploadPath).addResourceLocations(articleResourcePath);
    }

}





