package com.socket.company.controller;

import com.socket.company.dto.CompanyMembership;
import com.socket.company.service.CompanyMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class CompanyMembershipController {
    private final CompanyMembershipService service;

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUser(@RequestParam Long companyId,
                                        @RequestParam String ownerId,
                                        @RequestParam String userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.inviteUser(companyId, ownerId, userId));
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeInvitation(@RequestParam Long companyId,
                                              @RequestParam String ownerId,
                                              @RequestParam String userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.revokeInvitation(companyId, ownerId, userId));
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveRequest(@RequestParam Long companyId,
                                            @RequestParam String ownerId,
                                            @RequestParam String userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.approveRequest(companyId, ownerId, userId));
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectRequest(@RequestParam Long companyId,
                                           @RequestParam String ownerId,
                                           @RequestParam String userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.rejectRequest(companyId, ownerId, userId));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeUser(@RequestParam Long companyId,
                                        @RequestParam String ownerId,
                                        @RequestParam String userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.removeUser(companyId, ownerId, userId));
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestToJoin(@RequestParam Long companyId,
                                           @RequestParam String userId) {
        return ResponseEntity.ok(service.requestToJoin(companyId, userId));
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelRequest(@RequestParam Long companyId,
                                           @RequestParam String userId) {
        return ResponseEntity.ok(service.cancelRequest(companyId, userId));
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvitation(@RequestParam Long companyId,
                                              @RequestParam String userId) {
        return ResponseEntity.ok(service.acceptInvitation(companyId, userId));
    }

    @PostMapping("/decline")
    public ResponseEntity<?> declineInvitation(@RequestParam Long companyId,
                                               @RequestParam String userId) {
        return ResponseEntity.ok(service.declineInvitation(companyId, userId));
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveCompany(@RequestParam Long companyId,
                                          @RequestParam String userId) {
        return ResponseEntity.ok(service.leaveCompany(companyId, userId));
    }

    @GetMapping("/my/requests")
    public ResponseEntity<List<CompanyMembership>> getUserRequests(@RequestParam String userId) {
        return ResponseEntity.ok(service.getUserRequests(userId));
    }

    @GetMapping("/my/invitations")
    public ResponseEntity<List<CompanyMembership>> getUserInvitations(@RequestParam String userId) {
        return ResponseEntity.ok(service.getUserInvitations(userId));
    }

    @GetMapping("/company/{companyId}/requests")
    public ResponseEntity<List<CompanyMembership>> getCompanyRequests(@PathVariable Long companyId,
                                                                      @RequestParam String ownerId) throws AccessDeniedException {
        return ResponseEntity.ok(service.getCompanyRequests(companyId, ownerId));
    }

    @GetMapping("/company/{companyId}/invitations")
    public ResponseEntity<List<CompanyMembership>> getCompanyInvitations(@PathVariable Long companyId,
                                                                         @RequestParam String ownerId) throws AccessDeniedException {
        return ResponseEntity.ok(service.getCompanyInvitations(companyId, ownerId));
    }

    @GetMapping("/company/{companyId}/members")
    public ResponseEntity<List<CompanyMembership>> getCompanyMembers(@PathVariable Long companyId) {
        return ResponseEntity.ok(service.getCompanyMembers(companyId));
    }

    @PostMapping("/admin/appoint")
    public ResponseEntity<?> appointAdmin(@RequestParam Long companyId,
                                          @RequestParam String ownerId,
                                          @RequestParam String userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.appointAdmin(companyId, ownerId, userId));
    }

    @PostMapping("/admin/remove")
    public ResponseEntity<?> removeAdmin(@RequestParam Long companyId,
                                         @RequestParam String ownerId,
                                         @RequestParam String userId) throws AccessDeniedException {
        return ResponseEntity.ok(service.removeAdmin(companyId, ownerId, userId));
    }

    @GetMapping("/admins")
    public ResponseEntity<?> listAdmins(@RequestParam Long companyId,
                                        @RequestParam String ownerId) throws AccessDeniedException {
        return ResponseEntity.ok(service.getCompanyAdmins(companyId, ownerId));
    }
}