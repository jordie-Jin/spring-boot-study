package com.sesac.ai.backend.config;

import com.sesac.ai.backend.util.MessageFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MessageFormatter messageFormatter() { return new MessageFormatter(); }
}
