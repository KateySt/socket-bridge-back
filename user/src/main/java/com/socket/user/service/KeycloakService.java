package com.socket.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.socket.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final KeycloakProperties properties;

    public ResponseEntity<?> getAllUsers() {
        String adminToken = getAdminAccessToken();
        if (adminToken == null) return internalError("Failed to get admin token");

        String url = buildUrl("/admin/realms/" + properties.realm() + "/users");

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return internalError("Failed to get user list");
        }
    }

    public ResponseEntity<?> registerAndLogin(RegisterRequest req) {
        return tryOrError(() -> {
            String adminToken = getAdminAccessToken();
            if (adminToken == null) return internalError("Failed to get admin token");

            boolean created = createUser(adminToken, req);
            if (!created) return internalError("Failed to create user in Keycloak");

            Map<String, Object> userTokens = getUserToken(req.username(), req.password());
            if (userTokens == null) return unauthorized("Failed to authenticate created user");

            return ResponseEntity.ok(userTokens);
        });
    }

    private String getAdminAccessToken() {
        String url = buildUrl("/realms/master/protocol/openid-connect/token");
        String body = "grant_type=password" +
                "&client_secret=" + URLEncoder.encode(properties.clientSecret(), StandardCharsets.UTF_8) +
                "&client_id=" + URLEncoder.encode(properties.adminClientId(), StandardCharsets.UTF_8) +
                "&username=" + URLEncoder.encode(properties.adminUsername(), StandardCharsets.UTF_8) +
                "&password=" + URLEncoder.encode(properties.adminPassword(), StandardCharsets.UTF_8);
        HttpEntity<String> request = new HttpEntity<>(body, urlEncodedHeaders());

        return postForToken(url, request);
    }

    private Map<String, Object> getUserToken(String username, String password) {
        String url = buildUrl("/realms/" + properties.realm() + "/protocol/openid-connect/token");
        String body = "grant_type=password" +
                "&client_id=" + URLEncoder.encode(properties.userClientId(), StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(properties.clientSecret(), StandardCharsets.UTF_8) +
                "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8) +
                "&scope=openid";
        HttpEntity<String> request = new HttpEntity<>(body, urlEncodedHeaders());

        return postForBody(url, request);
    }

    private boolean createUser(String token, RegisterRequest user) {
        String url = buildUrl("/admin/realms/" + properties.realm() + "/users");
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);

        String json = String.format("""
                {
                  "username": "%s",
                  "enabled": true,
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "emailVerified": true,
                  "credentials": [
                    {
                      "type": "password",
                      "value": "%s",
                      "temporary": false
                    }
                  ]
                }
                """, user.username(), user.firstName(), user.lastName(), user.email(), user.password());
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) return false;

        String location = response.getHeaders().getFirst("Location");
        if (location == null) return false;

        String userId = location.substring(location.lastIndexOf("/") + 1);
        return assignRoleToUser(token, userId, Role.USER);
    }

    public ResponseEntity<?> login(LoginRequest request) {
        if (request.username() == null || request.password() == null) {
            return badRequest("Username and password required");
        }

        return tryOrError(() -> {
            Map<String, Object> tokens = getUserToken(request.username(), request.password());
            if (tokens == null) return unauthorized("Invalid credentials");
            return ResponseEntity.ok(tokens);
        });
    }

    public ResponseEntity<?> getUserInfo(String rawToken) {
        DecodedJWT jwt = JWT.decode(rawToken);
        String username = jwt.getClaim("preferred_username").asString();

        String adminToken = getAdminAccessToken();
        if (adminToken == null) return internalError("Failed to get admin token");

        String url = buildUrl("/admin/realms/" + properties.realm() + "/users?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8));

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);
            List<?> users = response.getBody();

            if (users == null || users.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(users.getFirst());
        } catch (Exception e) {
            e.printStackTrace();
            return internalError("Failed to get user");
        }
    }

    public ResponseEntity<?> updateUser(String userId, UpdateRequest updateData) {
        String adminToken = getAdminAccessToken();
        if (adminToken == null) return internalError("Failed to get admin token");

        String url = buildUrl("/admin/realms/" + properties.realm() + "/users/" + userId);

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(adminToken);

        String json = String.format("""
                {
                  "firstName": "%s",
                  "lastName": "%s"
                }
                """, updateData.firstName(), updateData.lastName());

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return internalError("Failed to update user");
        }
    }

    public ResponseEntity<?> deleteUser(String userId) {
        String adminToken = getAdminAccessToken();
        if (adminToken == null) return internalError("Failed to get admin token");

        String url = buildUrl("/admin/realms/" + properties.realm() + "/users/" + userId);

        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return internalError("Failed to delete user");
        }
    }

    public Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {
        String url = buildUrl("/realms/" + properties.realm() + "/protocol/openid-connect/token");

        String body = "grant_type=authorization_code" +
                "&client_id=" + URLEncoder.encode(properties.userClientId(), StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(properties.clientSecret(), StandardCharsets.UTF_8) +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        HttpEntity<String> request = new HttpEntity<>(body, urlEncodedHeaders());

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean assignRoleToUser(String token, String userId, Role role) {
        String realm = properties.realm();

        String roleUrl = buildUrl("/admin/realms/" + realm + "/roles/" + role.name());
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Map> roleResponse = restTemplate.exchange(roleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        if (!roleResponse.getStatusCode().is2xxSuccessful() || roleResponse.getBody() == null) return false;

        Map<String, Object> roleRepresentation = roleResponse.getBody();

        String assignUrl = buildUrl("/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm");
        HttpEntity<List<Map<String, Object>>> assignRequest = new HttpEntity<>(List.of(roleRepresentation), headers);

        try {
            restTemplate.postForEntity(assignUrl, assignRequest, Void.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String buildUrl(String path) {
        return properties.url() + path;
    }

    private HttpHeaders urlEncodedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String postForToken(String url, HttpEntity<String> request) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> body = response.getBody();
            return body != null ? (String) body.get("access_token") : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> postForBody(String url, HttpEntity<String> request) {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean postForStatus(String url, HttpEntity<?> request) {
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ResponseEntity<String> badRequest(String message) {
        return ResponseEntity.badRequest().body(message);
    }

    private ResponseEntity<String> internalError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }

    private ResponseEntity<String> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    private ResponseEntity<?> tryOrError(Supplier<ResponseEntity<?>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            e.printStackTrace();
            return internalError("Unexpected error");
        }
    }
}

