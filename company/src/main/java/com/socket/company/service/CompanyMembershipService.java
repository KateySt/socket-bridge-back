package com.socket.company.service;

import com.socket.company.dto.Company;
import com.socket.company.dto.CompanyMembership;
import com.socket.company.dto.CompanyRole;
import com.socket.company.dto.MembershipStatus;
import com.socket.company.repo.CompanyMembershipRepository;
import com.socket.company.repo.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyMembershipService {

    private final CompanyMembershipRepository membershipRepository;
    private final CompanyRepository companyRepository;

    public CompanyMembership inviteUser(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);

        var membership = membershipRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElse(new CompanyMembership(companyId, userId, MembershipStatus.INVITED));

        membership.setStatus(MembershipStatus.INVITED);
        updateTimestamps(membership);
        return membershipRepository.save(membership);
    }

    public CompanyMembership revokeInvitation(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership invitation = findMembership(companyId, userId, MembershipStatus.INVITED);
        invitation.setStatus(MembershipStatus.REVOKED);
        updateTimestamps(invitation);
        return membershipRepository.save(invitation);
    }

    public CompanyMembership approveRequest(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership request = findMembership(companyId, userId, MembershipStatus.REQUESTED);
        request.setStatus(MembershipStatus.ACCEPTED);
        updateTimestamps(request);
        return membershipRepository.save(request);
    }

    public CompanyMembership rejectRequest(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership request = findMembership(companyId, userId, MembershipStatus.REQUESTED);
        request.setStatus(MembershipStatus.REJECTED);
        updateTimestamps(request);
        return membershipRepository.save(request);
    }

    public CompanyMembership removeUser(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership member = findMembership(companyId, userId, MembershipStatus.ACCEPTED);
        member.setStatus(MembershipStatus.REMOVED);
        updateTimestamps(member);
        return membershipRepository.save(member);
    }

    public CompanyMembership requestToJoin(Long companyId, String userId) {
        var membership = membershipRepository.findByCompanyIdAndUserId(companyId, userId)
                .orElse(new CompanyMembership(companyId, userId, MembershipStatus.REQUESTED));

        membership.setStatus(MembershipStatus.REQUESTED);
        updateTimestamps(membership);
        return membershipRepository.save(membership);
    }

    public CompanyMembership cancelRequest(Long companyId, String userId) {
        CompanyMembership request = findMembership(companyId, userId, MembershipStatus.REQUESTED);
        request.setStatus(MembershipStatus.CANCELED);
        updateTimestamps(request);
        return membershipRepository.save(request);
    }

    public CompanyMembership acceptInvitation(Long companyId, String userId) {
        CompanyMembership invitation = findMembership(companyId, userId, MembershipStatus.INVITED);
        invitation.setStatus(MembershipStatus.ACCEPTED);
        updateTimestamps(invitation);
        return membershipRepository.save(invitation);
    }

    public CompanyMembership declineInvitation(Long companyId, String userId) {
        CompanyMembership invitation = findMembership(companyId, userId, MembershipStatus.INVITED);
        invitation.setStatus(MembershipStatus.DECLINED);
        updateTimestamps(invitation);
        return membershipRepository.save(invitation);
    }

    public CompanyMembership leaveCompany(Long companyId, String userId) {
        CompanyMembership member = findMembership(companyId, userId, MembershipStatus.ACCEPTED);
        member.setStatus(MembershipStatus.LEFT);
        updateTimestamps(member);
        return membershipRepository.save(member);
    }

    public List<CompanyMembership> getUserRequests(String userId) {
        return membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.REQUESTED);
    }

    public List<CompanyMembership> getUserInvitations(String userId) {
        return membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.INVITED);
    }

    public List<CompanyMembership> getCompanyRequests(Long companyId, String ownerId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        return membershipRepository.findByCompanyIdAndStatus(companyId, MembershipStatus.REQUESTED);
    }

    public List<CompanyMembership> getCompanyInvitations(Long companyId, String ownerId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        return membershipRepository.findByCompanyIdAndStatus(companyId, MembershipStatus.INVITED);
    }

    public List<CompanyMembership> getCompanyMembers(Long companyId) {
        return membershipRepository.findByCompanyIdAndStatus(companyId, MembershipStatus.ACCEPTED);
    }

    public CompanyMembership appointAdmin(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);

        CompanyMembership member = membershipRepository.findByCompanyIdAndUserIdAndStatus(companyId, userId, MembershipStatus.ACCEPTED)
                .orElseThrow(() -> new RuntimeException("User is not an accepted member"));

        if (member.getRole() == CompanyRole.ADMIN) {
            throw new IllegalStateException("User is already an admin");
        }

        member.setRole(CompanyRole.ADMIN);
        return membershipRepository.save(member);
    }

    public CompanyMembership removeAdmin(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);

        CompanyMembership member = membershipRepository.findByCompanyIdAndUserIdAndStatus(companyId, userId, MembershipStatus.ACCEPTED)
                .orElseThrow(() -> new RuntimeException("User is not an accepted member"));

        if (member.getRole() != CompanyRole.ADMIN) {
            throw new IllegalStateException("User is not an admin");
        }

        member.setRole(CompanyRole.MEMBER);
        return membershipRepository.save(member);
    }

    public List<CompanyMembership> getCompanyAdmins(Long companyId, String ownerId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        return membershipRepository.findByCompanyIdAndRole(companyId, CompanyRole.ADMIN);
    }

    private void validateOwner(Long companyId, String ownerId) throws AccessDeniedException {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        if (!company.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You are not the owner of this company");
        }
    }

    private CompanyMembership findMembership(Long companyId, String userId, MembershipStatus expectedStatus) {
        return membershipRepository.findByCompanyIdAndUserIdAndStatus(companyId, userId, expectedStatus)
                .orElseThrow(() -> new RuntimeException("Membership with status " + expectedStatus + " not found"));
    }

    private void updateTimestamps(CompanyMembership membership) {
        LocalDateTime now = LocalDateTime.now();
        if (membership.getCreatedAt() == null) {
            membership.setCreatedAt(now);
        }
        membership.setUpdatedAt(now);
    }
}