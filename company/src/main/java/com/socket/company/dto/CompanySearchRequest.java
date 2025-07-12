package com.socket.company.dto;

public record CompanySearchRequest(
        String query,
        int page,
        int size
) {
}
