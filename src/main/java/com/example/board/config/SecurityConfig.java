package com.example.board.config;

import com.example.board.dto.UserAccountDto;
import com.example.board.dto.security.BoardPrincipal;
import com.example.board.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.PutMapping;

@Configuration
public class SecurityConfig {

    // 과거에는 WebSecurityCustomizer에 따로 정적 리소스를 web.ignore() 설정해주었지만, 해당 방법은 시큐리티의 필터 체인에 등록되지 않아서 시큐리티의 다른 보안 설정들이 적용되지 않는 문제가 있어 권장하지 않게됨(csrf 방어가 적용되지 않는다던가) - 실제로 애플리케이션 실행 시 추천하지 않는다고 warn 로그를 발생시킨다.
    // 그래서 현재는 SecurityFilterChain에 정적 리소스를 permitAll()로 등록해주는 방식을 추천한다.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()  // 정적 리소스 경로는 모두 허용
                        .mvcMatchers(  // mvcMatchers()는 antMatchers()와 완벽히 호환이 되면서 스프링 패턴 매칭에 들어가는 룰들이 추가되었기 때문에 권장
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin().and()
                .logout().logoutSuccessUrl("/").and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return username -> userAccountRepository
                .findById(username)  // DB에 있는 유저 정보를 가져와서
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)  // 인증용 Principal 클래스로 변경
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니 - username: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {  // 스프링 시큐리티가 제공하는 암호화 모듈
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();  // 스프링 시큐리티에서 제공하는 PasswordEncoder 설정을 PasswordEncoderFactories로 부터 위임해서 가져옴
    }
}
