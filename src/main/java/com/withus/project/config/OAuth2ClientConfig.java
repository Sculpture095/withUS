package com.withus.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import static org.springframework.security.config.oauth2.client.CommonOAuth2Provider.GOOGLE;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleRegistration = GOOGLE.getBuilder("google")
                .clientId("206353284073-8kq6dbe1v9bf0rspaof2m0m7kco9dmvr.apps.googleusercontent.com")
                .clientSecret("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")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("email", "profile")
                .build();
        return new InMemoryClientRegistrationRepository(googleRegistration);
    }
}

