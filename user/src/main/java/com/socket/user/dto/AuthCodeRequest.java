package com.socket.user.dto;

public record AuthCodeRequest(
        String code,
        String redirectUri
) {}
