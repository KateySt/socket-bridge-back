package com.socket.user.service;

import com.socket.user.dto.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final KeycloakProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAdminToken() {
        String url = props.url() + "/realms/master/protocol/openid-connect/token";
        String body = "grant_type=password" +
                "&client_id=" + props.adminClientId() +
                "&client_secret=" + props.clientSecret() +
                "&username=" + props.adminUsername() +
                "&password=" + props.adminPassword();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to get admin token");
        }
        return (String) response.getBody().get("access_token");
    }
}
