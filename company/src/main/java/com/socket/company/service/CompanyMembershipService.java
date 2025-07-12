package com.socket.company.service;

import com.socket.company.entity.CompanyMembership;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CompanyMembershipService {

    List<String> getCompanyUserIds(Long companyId);

    CompanyMembership inviteUser(Long companyId, String ownerId, String userId) throws AccessDeniedException;

    CompanyMembership revokeInvitation(Long companyId, String ownerId, String userId) throws AccessDeniedException;

    CompanyMembership approveRequest(Long companyId, String ownerId, String userId) throws AccessDeniedException;

    CompanyMembership rejectRequest(Long companyId, String ownerId, String userId) throws AccessDeniedException;

    CompanyMembership removeUser(Long companyId, String ownerId, String userId) throws AccessDeniedException;

    CompanyMembership requestToJoin(Long companyId, String userId);

    CompanyMembership cancelRequest(Long companyId, String userId);

    CompanyMembership acceptInvitation(Long companyId, String userId);

    CompanyMembership declineInvitation(Long companyId, String userId);

    CompanyMembership leaveCompany(Long companyId, String userId);

    List<CompanyMembership> getUserRequests(String userId);

    List<CompanyMembership> getUserInvitations(String userId);

    List<CompanyMembership> getCompanyRequests(Long companyId, String ownerId) throws AccessDeniedException;

    List<CompanyMembership> getCompanyInvitations(Long companyId, String ownerId) throws AccessDeniedException;

    List<CompanyMembership> getCompanyMembers(Long companyId);

    CompanyMembership appointAdmin(Long companyId, String ownerId, String userId) throws AccessDeniedException;

    CompanyMembership removeAdmin(Long companyId, String ownerId, String userId) throws AccessDeniedException;

    boolean isOwnerOrAdmin(Long companyId, String userId);

    List<CompanyMembership> getCompanyAdmins(Long companyId, String ownerId) throws AccessDeniedException;
}
