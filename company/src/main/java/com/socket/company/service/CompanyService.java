package com.socket.company.service;

import com.socket.company.dto.CompanyResponse;
import com.socket.company.dto.CreateCompany;
import com.socket.company.entity.Company;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

public interface CompanyService {

    Company createCompany(CreateCompany company, String ownerId);

    void assignOwnerRole(String userId);

    Optional<CompanyResponse> getCompany(Long id);

    Page<Company> listCompanies(String query, int page, int size);

    Company updateCompany(Long id, CreateCompany updated, String ownerId) throws AccessDeniedException;

    void deleteCompany(Long id, String ownerId) throws AccessDeniedException;
}
