package com.socket.company.service;

import com.socket.company.dto.Company;
import com.socket.company.dto.CreateCompany;
import com.socket.company.repo.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company createCompany(CreateCompany company, String ownerId) {
        var createCompany = Company.builder()
                .name(company.name())
                .ownerId(ownerId)
                .description(company.description())
                .visible(company.visible())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return companyRepository.save(createCompany);
    }

    public Optional<Company> getCompany(Long id) {
        return companyRepository.findById(id);
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
        existing.setUpdatedAt(LocalDateTime.now());

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
