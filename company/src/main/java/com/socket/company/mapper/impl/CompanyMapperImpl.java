package com.socket.company.mapper.impl;

import com.socket.company.dto.CompanyResponse;
import com.socket.company.dto.CreateCompany;
import com.socket.company.entity.Company;
import com.socket.company.mapper.CompanyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyMapperImpl implements CompanyMapper {

    @Override
    public Company toCompany(CreateCompany company, String ownerId) {
        return Company.builder()
                .name(company.name())
                .ownerId(ownerId)
                .description(company.description())
                .visible(company.visible())
                .build();
    }

    @Override
    public CompanyResponse toCompanyResponse(Company company, List<String> adminIds) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .visible(company.isVisible())
                .ownerId(company.getOwnerId())
                .adminIds(adminIds)
                .build();
    }
}
