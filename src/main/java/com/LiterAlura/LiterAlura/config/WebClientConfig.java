package com.LiterAlura.LiterAlura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient gutendexClient() {
        return WebClient.builder()
                .baseUrl("https://gutendex.com")
                .build();
    }
}
