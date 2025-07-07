package com.socket.company.service;

import com.socket.company.dto.*;
import com.socket.company.repo.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final RestTemplate restTemplate;
    private final CompanyRepository companyRepository;

    public Company createCompany(CreateCompany company, String ownerId) {
        assignOwnerRole(ownerId);

        var createCompany = Company.builder()
                .name(company.name())
                .ownerId(ownerId)
                .description(company.description())
                .visible(company.visible())
                .build();
        return companyRepository.save(createCompany);
    }

    public void assignOwnerRole(String userId) {
        String url = "http://USER/api/user/" + userId + "/role?role=OWNER";
        restTemplate.postForEntity(url, null, Void.class);
    }

    public Optional<CompanyResponse> getCompany(Long id) {
        return companyRepository.findById(id).map(company -> {
            List<String> adminIds = company.getMemberships().stream()
                    .filter(m -> m.getRole() == CompanyRole.ADMIN)
                    .map(CompanyMembership::getUserId)
                    .collect(Collectors.toList());

            return CompanyResponse.builder()
                    .id(company.getId())
                    .name(company.getName())
                    .description(company.getDescription())
                    .visible(company.isVisible())
                    .ownerId(company.getOwnerId())
                    .adminIds(adminIds)
                    .build();
        });
    }

    public List<Company> listCompanies(String ownerId) {
        return companyRepository.findAllByVisibleIsTrueOrOwnerId(ownerId);
    }

    public Company updateCompany(Long id, CreateCompany updated, String ownerId) throws AccessDeniedException {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner");
        }

        existing.setName(updated.name());
        existing.setDescription(updated.description());
        existing.setVisible(updated.visible());

        return companyRepository.save(existing);
    }

    public void deleteCompany(Long id, String ownerId) throws AccessDeniedException {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner");
        }

        companyRepository.delete(existing);
    }
}
