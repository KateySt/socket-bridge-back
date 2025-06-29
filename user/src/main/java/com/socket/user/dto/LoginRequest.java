package com.socket.user.dto;

public record LoginRequest(
        String username,
        String password
) {
}
