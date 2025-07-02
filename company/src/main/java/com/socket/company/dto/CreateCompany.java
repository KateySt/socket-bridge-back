package com.socket.company.dto;

public record CreateCompany(
        String name,
        String description,
        boolean visible
) {
}
