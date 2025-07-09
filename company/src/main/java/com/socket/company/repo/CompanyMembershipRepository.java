package com.socket.company.repo;

import com.socket.company.entity.CompanyMembership;
import com.socket.company.enums.CompanyRole;
import com.socket.company.enums.MembershipStatus;
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

    List<CompanyMembership> findByCompanyId(Long companyId);
}