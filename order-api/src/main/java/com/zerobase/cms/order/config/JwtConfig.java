package com.zerobase.cms.order.config;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    // 다른 모듈의 클래스이기 때문에 따로 Bean 등록 필요

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }
}
