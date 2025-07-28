package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "User_Membership")
@Data
public class UserMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long membershipId;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;


    @ManyToOne
    @JoinColumn(name = "membership_package_id", nullable = false)
    private MembershipPackage membershipPackageId;


    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Trạng thái: ACTIVE, EXPIRED, CANCELED,...
    @Column(name = "status")
    private String status;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

}
