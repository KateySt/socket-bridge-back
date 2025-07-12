package com.socket.company.mapper;

import com.socket.company.dto.CompanyResponse;
import com.socket.company.dto.CreateCompany;
import com.socket.company.entity.Company;

import java.util.List;

public interface CompanyMapper {
    Company toCompany(CreateCompany company, String ownerId);

    CompanyResponse toCompanyResponse(Company company, List<String> adminIds);
}
