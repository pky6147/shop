package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /*Application.properties에서  쓴 파라미터 값 읽어옴*/
    
    @Value("${uploadPath}")
    String uploadPath;
    /*서버 컴퓨터의 파일 시스템에 위치하는 자원(사진)을 요청 url과 매핑해서 응답하도록 함*/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        /*http요청 "/images/**" 이 들어오면
         uploadPath(file:///C:/shop/에서 동일한 리소스를 찾아서 반환(응답)한다.
         EX1 http GET /images/abc.jpg
         응답:  C:/shop/abc.jpg 자원을 직접 응답

         */



        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
    }
    
}
