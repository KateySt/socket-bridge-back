package com.socket.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName
) {
}
