package com.socket.company.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long companyId;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    @Enumerated(EnumType.STRING)
    private CompanyRole role = CompanyRole.MEMBER;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public CompanyMembership(Long companyId, String userId, MembershipStatus status) {
        this.companyId = companyId;
        this.userId = userId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}