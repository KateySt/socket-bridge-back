package com.socket.company.service.impl;

import com.socket.company.dto.CompanyResponse;
import com.socket.company.dto.CreateCompany;
import com.socket.company.entity.Company;
import com.socket.company.entity.CompanyMembership;
import com.socket.company.enums.CompanyRole;
import com.socket.company.mapper.CompanyMapper;
import com.socket.company.repo.CompanyRepository;
import com.socket.company.service.CompanySearchService;
import com.socket.company.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final RestTemplate restTemplate;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanySearchService companySearchService;

    @Override
    public Company createCompany(CreateCompany company, String ownerId) {
        assignOwnerRole(ownerId);

        Company mapperCompany = companyMapper.toCompany(company, ownerId);

        Company saved = companyRepository.save(mapperCompany);

        companySearchService.indexCompany(saved);

        return saved;
    }

    @Override
    public void assignOwnerRole(String userId) {
        String url = "http://USER/api/user/" + userId + "/role?role=OWNER";
        restTemplate.postForEntity(url, null, Void.class);
    }

    @Override
    public Optional<CompanyResponse> getCompany(Long id) {
        return companyRepository.findById(id).map(company -> {
            List<String> adminIds = company.getMemberships().stream()
                    .filter(m -> m.getRole() == CompanyRole.ADMIN)
                    .map(CompanyMembership::getUserId)
                    .collect(Collectors.toList());

            return companyMapper.toCompanyResponse(company, adminIds);
        });
    }

    @Override
    public Page<Company> listCompanies(String query, int page, int size) {
        return companySearchService.search(query, page, size);
    }

    @Override
    public Company updateCompany(Long id, CreateCompany updated, String ownerId) throws AccessDeniedException {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner");
        }

        existing.setName(updated.name());
        existing.setDescription(updated.description());
        existing.setVisible(updated.visible());

        companySearchService.updateCompany(existing);

        return companyRepository.save(existing);
    }

    @Override
    public void deleteCompany(Long id, String ownerId) throws AccessDeniedException {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner");
        }
        companySearchService.deleteFromIndex(id);
        companyRepository.delete(existing);
    }
}
