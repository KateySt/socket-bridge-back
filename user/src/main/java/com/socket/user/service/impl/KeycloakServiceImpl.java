package com.socket.user.service.impl;

import com.socket.user.dto.LoginRequest;
import com.socket.user.dto.RegisterRequest;
import com.socket.user.dto.UpdateRequest;
import com.socket.user.enums.Role;
import com.socket.user.mapper.JsonTemplateBuilder;
import com.socket.user.service.*;
import com.socket.user.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final TokenService tokenService;
    private final KeycloakRestClient client;
    private final JsonTemplateBuilder jsonBuilder;

    @Override
    public ResponseEntity<?> registerAndLogin(RegisterRequest req) {
        return ResponseUtils.tryOrError(() -> {
            String adminToken = tokenService.getAdminToken();
            String userId = client.createUser(adminToken, jsonBuilder.buildCreateUserJson(req));
            client.assignRoleToUser(adminToken, userId, Role.USER);
            Map<String, Object> tokens = client.getUserToken(req.username(), req.password());
            return tokens != null ? ResponseEntity.ok(tokens) : ResponseUtils.unauthorized("Login failed");
        });
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        return ResponseUtils.tryOrError(() -> {
            Map<String, Object> tokens = client.getUserToken(request.username(), request.password());
            return tokens != null ? ResponseEntity.ok(tokens) : ResponseUtils.unauthorized("Invalid credentials");
        });
    }

    @Override
    public ResponseEntity<?> assignUserRole(String userId, String roleName) {
        return ResponseUtils.tryOrError(() -> {
            String adminToken = tokenService.getAdminToken();
            Role role = Role.valueOf(roleName.toUpperCase());
            boolean success = client.assignRoleToUser(adminToken, userId, role);
            return success ? ResponseEntity.ok("Role assigned") : ResponseUtils.internalError("Failed to assign role");
        });
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        return ResponseUtils.tryOrError(() -> {
            String adminToken = tokenService.getAdminToken();
            return ResponseEntity.ok(client.getAllUsers(adminToken));
        });
    }

    @Override
    public ResponseEntity<?> getUserInfo(String rawToken) {
        return ResponseUtils.tryOrError(() -> {
            String username = client.decodeUsernameFromToken(rawToken);
            String adminToken = tokenService.getAdminToken();
            return ResponseEntity.ok(client.getUserByUsername(adminToken, username));
        });
    }

    @Override
    public ResponseEntity<?> updateUser(String userId, UpdateRequest updateData) {
        return ResponseUtils.tryOrError(() -> {
            String adminToken = tokenService.getAdminToken();
            boolean success = client.updateUser(adminToken, userId, jsonBuilder.buildUpdateUserJson(updateData));
            return success ? ResponseEntity.ok("User updated") : ResponseUtils.internalError("Update failed");
        });
    }

    @Override
    public ResponseEntity<?> deleteUser(String userId) {
        return ResponseUtils.tryOrError(() -> {
            String adminToken = tokenService.getAdminToken();
            boolean success = client.deleteUser(adminToken, userId);
            return success ? ResponseEntity.ok("User deleted") : ResponseUtils.internalError("Delete failed");
        });
    }

    @Override
    public Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {
        return client.exchangeCodeForToken(code, redirectUri);
    }
}