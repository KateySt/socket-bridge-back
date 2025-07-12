package com.socket.company.service;

import com.socket.company.entity.Company;
import org.springframework.data.domain.Page;

public interface CompanySearchService {
    Page<Company> search(String query, int page, int size);

    void indexCompany(Company company);

    void deleteFromIndex(Long companyId);

    void updateCompany(Company company);
}
