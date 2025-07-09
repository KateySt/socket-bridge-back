package com.socket.company.repo;

import com.socket.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByOwnerId(String ownerId);
    List<Company> findAllByVisibleIsTrueOrOwnerId(String ownerId);
}
