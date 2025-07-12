package com.socket.company.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.socket.company.entity.Company;
import com.socket.company.entity.CompanyDocument;
import com.socket.company.repo.CompanyRepository;
import com.socket.company.service.CompanySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompanySearchServiceImpl implements CompanySearchService {

    private final ElasticsearchClient elasticsearchClient;

    private final CompanyRepository companyRepository;

    private static final String INDEX_NAME = "companies";

    @Override
    public Page<Company> search(String query, int page, int size) {
        try {
            SearchResponse<CompanyDocument> response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> {
                                if (query == null || query.isBlank()) {
                                    return q.matchAll(m -> m);
                                } else {
                                    return q.match(m -> m.field("name").query(query));
                                }
                            })
                            .from(page * size)
                            .size(size),
                    CompanyDocument.class);

            List<CompanyDocument> hits = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();

            List<Long> ids = hits.stream().map(CompanyDocument::getId).toList();
            List<Company> companies = companyRepository.findAllById(ids);

            return new PageImpl<>(companies, PageRequest.of(page, size), response.hits().total().value());

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch search failed", e);
        }
    }

    @Override
    public void updateCompany(Company company) {
        CompanyDocument doc = CompanyDocument.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .ownerId(company.getOwnerId())
                .build();

        try {
            elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(company.getId().toString())
                    .document(doc));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update company in Elasticsearch", e);
        }
    }

    @Override
    public void indexCompany(Company company) {
        CompanyDocument doc = CompanyDocument.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .ownerId(company.getOwnerId())
                .build();

        try {
            elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(company.getId().toString())
                    .document(doc));
        } catch (IOException e) {
            throw new RuntimeException("Failed to index company", e);
        }
    }

    @Override
    public void deleteFromIndex(Long companyId) {
        try {
            elasticsearchClient.delete(d -> d.index(INDEX_NAME).id(companyId.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete from index", e);
        }
    }
}
