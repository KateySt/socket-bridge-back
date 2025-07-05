package com.socket.company.repo;

import com.socket.company.dto.CompanyMembership;
import com.socket.company.dto.CompanyRole;
import com.socket.company.dto.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyMembershipRepository extends JpaRepository<CompanyMembership, Long> {
    Optional<CompanyMembership> findByCompanyIdAndUserId(Long companyId, String userId);

    List<CompanyMembership> findByUserIdAndStatus(String userId, MembershipStatus membershipStatus);

    List<CompanyMembership> findByCompanyIdAndStatus(Long companyId, MembershipStatus membershipStatus);

    Optional<CompanyMembership> findByCompanyIdAndUserIdAndStatus(Long companyId, String userId, MembershipStatus status);

    List<CompanyMembership> findByCompanyIdAndRole(Long companyId, CompanyRole role);
}