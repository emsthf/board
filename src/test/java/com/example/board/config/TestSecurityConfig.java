package com.example.board.config;

import com.example.board.domain.UserAccount;
import com.example.board.repository.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserAccountRepository userAccountRepository;

    // @BeforeTestMethod로 인증과 관련된 테스트를 할 때에만 직전에 특정한 코드가 리스너를 통해서 실행되게 끔 만들 수 있다.
    // 스프링이 제공하는 어노테이션(시큐리티x)
    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "solTest",
                "qwer1234",
                "sol_test@gmail.com",
                "Ssol-test",
                "test memo"
        )));
    }
}
