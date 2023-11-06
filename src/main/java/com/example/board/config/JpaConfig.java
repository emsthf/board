package com.example.board.config;

import com.example.board.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())  // 시큐리티의 정보를 들고 있는 SecurityContextHolder에서
                .map(SecurityContext::getAuthentication)  // Authentication을 가져와서
                .filter(Authentication::isAuthenticated)  // 인증되었는지(로그인 한 상태인지) 확인하고
                .map(Authentication::getPrincipal)  // 인증된 사용자의 정보(Principal. 보통 Principal을 상속한 UserDetails를 사용)인터페이스를 가져와서
                .map(BoardPrincipal.class::cast)  // UserDetails를 구현한 BoardPrincipal로 캐스팅
                .map(BoardPrincipal::getUsername);
    }
}
