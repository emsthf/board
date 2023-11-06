package com.example.board.dto.security;

import com.example.board.dto.UserAccountDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record BoardPrincipal(
        String username,  // UserAccount의 userId와는 조금 다른 UserDetails에서 사용하는 사용자 이름 필드
        String password, // UserAccount의 userPassword와는 조금 다른 UserDetails에서 사용하는 사용자 비밀번호 필드
        Collection<? extends GrantedAuthority> authorities,  // GrantedAuthority는 인증된 사용자의 권한을 나타내는 인터페이스. 이것을 상속한 컬렉션을 필드에 추가해주고 getAuthorities()에 넣어줘서 getter로 사용
        String email,  // 직접 만든 UserAccount의 필드들
        String nickname,
        String memo
) implements UserDetails {  // UserDetails는 스프링 시큐리티의 사용자 정보를 담고 권한을 관리하는 인터페이스

    public static BoardPrincipal of(String username, String password, String email, String nickname, String memo) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new BoardPrincipal(
                username,
                password,
                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)  // GrantedAuthority를 구현한 SimpleGrantedAuthority를 사용
                        .collect(Collectors.toUnmodifiableSet()),  // 처음 인증이 통과 되었을 때, 한번 RoleType이 만들어짐
                email,
                nickname,
                memo
        );
    }

    // UserAccountDto <-> BoardPrincipal로 변환하는 팩토리 메서드들
    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }

    public UserAccountDto toDto() {
        return UserAccountDto.of(
                username,
                password,
                email,
                nickname,
                memo
        );
    }


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 아래 4개 메서드는 계정 만료, 잠금, 자격증명 만료, 활성화 여부를 반환하는 것인데
    // 프로젝트에서 해당 기능을 사용하지 않는다면 모두 true를 반환하도록 설정해서 스프링 시큐리티에서 해당 인증 통과 처리 한다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Getter
    public enum RoleType {
        USER("ROLE_USER");

        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }
}
