package com.socket.company.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CompanyResponse(
        Long id,
        String name,
        String description,
        String ownerId,
        boolean visible,
        List<String> adminIds
) {
}
