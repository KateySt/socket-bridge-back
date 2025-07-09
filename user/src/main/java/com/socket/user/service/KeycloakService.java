package com.socket.user.service;

import com.socket.user.dto.LoginRequest;
import com.socket.user.dto.RegisterRequest;
import com.socket.user.dto.UpdateRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface KeycloakService {
    ResponseEntity<?> registerAndLogin(RegisterRequest req);
    ResponseEntity<?> login(LoginRequest request);
    ResponseEntity<?> getAllUsers();
    ResponseEntity<?> assignUserRole(String userId, String roleName);
    ResponseEntity<?> getUserInfo(String token);
    ResponseEntity<?> updateUser(String userId, UpdateRequest update);
    ResponseEntity<?> deleteUser(String userId);
    Map<String, Object> exchangeCodeForToken(String code, String redirectUri);
}
