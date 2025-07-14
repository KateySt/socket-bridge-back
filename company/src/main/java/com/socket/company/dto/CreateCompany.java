package com.socket.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCompany(

        @NotBlank(message = "Company name is required")
        @Size(max = 100, message = "Company name must be at most 100 characters")
        String name,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        boolean visible

) {}
