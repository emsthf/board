package com.example.board.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

/**
 * Thymeleaf 3 Decoupled Logic 설정
 *
 * 이 설정은 spring boot의 Common Application Properties에 등록이 되어있지 않아서 직접 구현해야 한다.
 * 타임리프로 작성된 마크업에서 타임리프의 로직을 사용하지 않고 순수하게 마크업만 사용하고 싶을 때 사용한다.
 * 디자이너나 프론트에게 마크업 목업 파일을 전달할 때 유용
 */
@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver(
            SpringResourceTemplateResolver defaultTemplateResolver,
            Thymeleaf3Properties thymeleaf3Properties
    ) {
        defaultTemplateResolver.setUseDecoupledLogic(thymeleaf3Properties.isDecoupledLogic());

        return defaultTemplateResolver;
    }

    /**
     * 이 설정을 쉽게 켜고 끌 수 있도록 Application Properties로 노출시키는 방법
     */
    @RequiredArgsConstructor
    @Getter
    @ConstructorBinding
    @ConfigurationProperties("spring.thymeleaf3")  // ConfigurationProperties를 유저가 직접 만들었을 때는 반드시 스캔을 해줘야 한다. main 클래스에 @ConfigurationPropertiesScan을 붙여주면 된다. 이렇게 하면 'spring.thymeleaf3.decoupled-logic'를 application.properties에 추가해 사용할 수 있다.
    public static class Thymeleaf3Properties {
        /**
         * Use Thymeleaf 3 Decoupled Logic
         */
        private final boolean decoupledLogic;
    }
}
