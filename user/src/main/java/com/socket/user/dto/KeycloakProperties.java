package com.socket.user.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
        String url,
        String realm,
        String adminUsername,
        String adminPassword,
        String adminClientId,
        String userClientId,
        String clientSecret
) {
}