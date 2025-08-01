package com.socket.company.controller;

import com.socket.company.entity.Company;
import com.socket.company.dto.CompanyResponse;
import com.socket.company.dto.CreateCompany;
import com.socket.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<Company> createCompany(@Valid @RequestBody CreateCompany company, @RequestHeader("X-User-Id") String userId) {
        Company created = companyService.createCompany(company, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long id) {
        return companyService.getCompany(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<Company> listCompanies(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return companyService.listCompanies(query, page, size);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@Valid @PathVariable Long id, @RequestBody CreateCompany updated, @RequestHeader("X-User-Id") String userId) {
        try {
            Company company = companyService.updateCompany(id, updated, userId);
            return ResponseEntity.ok(company);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id, @RequestHeader("X-User-Id") String userId) {
        try {
            companyService.deleteCompany(id, userId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
