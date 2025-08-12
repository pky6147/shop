package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditAwareImple implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        /*AuditAware: Entity 생성 및 수정 시 해당 행위의 주체(유저)의 정보를 알아냄
          구현: 시큐리티 컨텍스트 -> Authentication -> 유저정보 -> 유저 아이디(이름) -> 반환*/
       SecurityContext context  = SecurityContextHolder.getContext();
       Authentication authentication = context.getAuthentication();
       String userId = "";
       if ( authentication != null) {
           userId = authentication.getName();
       }

        return Optional.of(userId);
    }
}
