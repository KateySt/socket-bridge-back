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
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyMembershipService {

    private final CompanyMembershipRepository membershipRepository;
    private final CompanyRepository companyRepository;

    public CompanyMembership inviteUser(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CompanyMembership membership = membershipRepository
                .findByCompanyIdAndUserId(companyId, userId)
                .orElseGet(() -> new CompanyMembership(userId, MembershipStatus.INVITED, company));

        membership.setStatus(MembershipStatus.INVITED);
        return membershipRepository.save(membership);
    }

    public CompanyMembership revokeInvitation(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership invitation = findMembership(companyId, userId, MembershipStatus.INVITED);
        invitation.setStatus(MembershipStatus.REVOKED);
        return membershipRepository.save(invitation);
    }

    public CompanyMembership approveRequest(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership request = findMembership(companyId, userId, MembershipStatus.REQUESTED);
        request.setStatus(MembershipStatus.ACCEPTED);
        return membershipRepository.save(request);
    }

    public CompanyMembership rejectRequest(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership request = findMembership(companyId, userId, MembershipStatus.REQUESTED);
        request.setStatus(MembershipStatus.REJECTED);
        return membershipRepository.save(request);
    }

    public CompanyMembership removeUser(Long companyId, String ownerId, String userId) throws AccessDeniedException {
        validateOwner(companyId, ownerId);
        CompanyMembership member = findMembership(companyId, userId, MembershipStatus.ACCEPTED);
        member.setStatus(MembershipStatus.REMOVED);
        return membershipRepository.save(member);
    }

    public CompanyMembership requestToJoin(Long companyId, String userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CompanyMembership membership = membershipRepository
                .findByCompanyIdAndUserId(companyId, userId)
                .orElseGet(() -> new CompanyMembership(userId, MembershipStatus.REQUESTED, company));

        membership.setStatus(MembershipStatus.REQUESTED);
        return membershipRepository.save(membership);
    }

    public CompanyMembership cancelRequest(Long companyId, String userId) {
        CompanyMembership request = findMembership(companyId, userId, MembershipStatus.REQUESTED);
        request.setStatus(MembershipStatus.CANCELED);
        return membershipRepository.save(request);
    }

    public CompanyMembership acceptInvitation(Long companyId, String userId) {
        CompanyMembership invitation = findMembership(companyId, userId, MembershipStatus.INVITED);
        invitation.setStatus(MembershipStatus.ACCEPTED);
        return membershipRepository.save(invitation);
    }

    public CompanyMembership declineInvitation(Long companyId, String userId) {
        CompanyMembership invitation = findMembership(companyId, userId, MembershipStatus.INVITED);
        invitation.setStatus(MembershipStatus.DECLINED);
        return membershipRepository.save(invitation);
    }

    public CompanyMembership leaveCompany(Long companyId, String userId) {
        CompanyMembership member = findMembership(companyId, userId, MembershipStatus.ACCEPTED);
        member.setStatus(MembershipStatus.LEFT);
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

    public boolean isOwnerOrAdmin(Long companyId, String userId) {
        var company = companyRepository.findById(companyId);
        if (company.isPresent() && company.get().getOwnerId().equals(userId)) {
            return true;
        }

        return membershipRepository.findByCompanyIdAndUserIdAndStatus(companyId, userId, MembershipStatus.ACCEPTED)
                .map(m -> m.getRole() == CompanyRole.ADMIN)
                .orElse(false);
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
}