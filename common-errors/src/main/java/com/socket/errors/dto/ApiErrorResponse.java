package com.socket.errors.dto;

public record ApiErrorResponse(
        int status,
        String message
) {}
