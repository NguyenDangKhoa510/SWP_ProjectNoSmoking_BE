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

    // FK tới bảng members (user_id)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    // FK tới bảng tblMembershipPackage (MembershipPackage_Id)
    @ManyToOne
    @JoinColumn(name = "membership_package_id", nullable = false)
    private MembershipPackage membershipPackage;

    // Có thể thêm ngày đăng ký và ngày kết thúc nếu cần
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Trạng thái: ACTIVE, EXPIRED, CANCELED,...
    @Column(name = "status")
    private String status;
}
