package com.socket.company.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socket.company.enums.CompanyRole;
import com.socket.company.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "company_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CompanyMembership extends TimeStampedModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    @Enumerated(EnumType.STRING)
    private CompanyRole role = CompanyRole.MEMBER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnore
    private Company company;

    public CompanyMembership(String userId, MembershipStatus status, Company company) {
        this.userId = userId;
        this.status = status;
        this.company = company;
    }
}