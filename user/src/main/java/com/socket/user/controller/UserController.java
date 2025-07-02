package com.socket.user.controller;

import com.socket.user.dto.LoginRequest;
import com.socket.user.dto.RegisterRequest;
import com.socket.user.dto.UpdateRequest;
import com.socket.user.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final KeycloakService keycloakService;

    @Value("${frontend.home-url}")
    private String urlFront;

    @Value("${base-url}")
    private String urlBase;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return keycloakService.getAllUsers();
    }

    @GetMapping("/exchange-code")
    public ResponseEntity<?> exchangeCodeForToken(
            @RequestParam("code") String code) {

        Map<String, Object> tokenResponse = keycloakService.exchangeCodeForToken(code, urlBase + "/user/api/exchange-code");
        if (tokenResponse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to get token");
        }
        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(3600)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(86400)
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(urlFront).toString())
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        return keycloakService.registerAndLogin(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return keycloakService.login(request);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replaceFirst("(?i)^Bearer\\s+", "");
        return keycloakService.getUserInfo(token);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UpdateRequest update) {
        return keycloakService.updateUser(id, update);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return keycloakService.deleteUser(id);
    }
}