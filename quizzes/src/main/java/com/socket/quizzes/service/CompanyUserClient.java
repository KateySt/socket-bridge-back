package com.socket.quizzes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyUserClient {
    private final RestTemplate rest;

    public List<String> getCompanyUsers(Long companyId) {
        return rest.getForObject("http://company/api/memberships/users?companyId=" + companyId, List.class);
    }
    public String getUserEmail(String userId) {
        return rest.getForObject("http://user/api/users/" + userId + "/email", String.class);
    }
}

