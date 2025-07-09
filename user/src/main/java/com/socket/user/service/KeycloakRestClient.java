package com.socket.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.socket.user.dto.KeycloakProperties;
import com.socket.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakRestClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final KeycloakProperties props;

    public String createUser(String token, String json) {
        String url = props.url() + "/admin/realms/" + props.realm() + "/users";
        HttpHeaders headers = jsonHeaders(token);
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) throw new RuntimeException("Failed to create user");

        String location = response.getHeaders().getFirst("Location");
        if (location == null) throw new RuntimeException("No Location header from create user");
        return location.substring(location.lastIndexOf('/') + 1);
    }

    public boolean assignRoleToUser(String token, String userId, Role role) {
        String realm = props.realm();

        String roleUrl = props.url() + "/admin/realms/" + realm + "/roles/" + role.name();
        HttpEntity<Void> getRequest = new HttpEntity<>(jsonHeaders(token));
        ResponseEntity<Map> roleResponse = restTemplate.exchange(roleUrl, HttpMethod.GET, getRequest, Map.class);

        if (!roleResponse.getStatusCode().is2xxSuccessful() || roleResponse.getBody() == null) return false;

        String assignUrl = props.url() + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        HttpEntity<List<Map<String, Object>>> assignRequest = new HttpEntity<>(List.of(roleResponse.getBody()), jsonHeaders(token));
        restTemplate.postForEntity(assignUrl, assignRequest, Void.class);
        return true;
    }

    public Map<String, Object> getUserToken(String username, String password) {
        try {
            String url = props.url() + "/realms/" + props.realm() + "/protocol/openid-connect/token";
            String body = "grant_type=password" +
                    "&client_id=" + URLEncoder.encode(props.userClientId(), StandardCharsets.UTF_8) +
                    "&client_secret=" + URLEncoder.encode(props.clientSecret(), StandardCharsets.UTF_8) +
                    "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                    "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8) +
                    "&scope=openid";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<?> getAllUsers(String adminToken) {
        String url = props.url() + "/admin/realms/" + props.realm() + "/users";
        HttpEntity<Void> request = new HttpEntity<>(jsonHeaders(adminToken));
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
        return response.getBody();
    }

    public Object getUserByUsername(String adminToken, String username) {
        String url = props.url() + "/admin/realms/" + props.realm() + "/users?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);
        HttpEntity<Void> request = new HttpEntity<>(jsonHeaders(adminToken));
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
        List<?> users = response.getBody();
        return (users != null && !users.isEmpty()) ? users.get(0) : null;
    }

    public boolean updateUser(String adminToken, String userId, String json) {
        try {
            String url = props.url() + "/admin/realms/" + props.realm() + "/users/" + userId;
            HttpEntity<String> request = new HttpEntity<>(json, jsonHeaders(adminToken));
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String adminToken, String userId) {
        try {
            String url = props.url() + "/admin/realms/" + props.realm() + "/users/" + userId;
            HttpEntity<Void> request = new HttpEntity<>(jsonHeaders(adminToken));
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {
        try {
            String url = props.url() + "/realms/" + props.realm() + "/protocol/openid-connect/token";
            String body = "grant_type=authorization_code" +
                    "&client_id=" + URLEncoder.encode(props.userClientId(), StandardCharsets.UTF_8) +
                    "&client_secret=" + URLEncoder.encode(props.clientSecret(), StandardCharsets.UTF_8) +
                    "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                    "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decodeUsernameFromToken(String rawToken) {
        DecodedJWT jwt = JWT.decode(rawToken);
        return jwt.getClaim("preferred_username").asString();
    }

    private HttpHeaders jsonHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }
}
