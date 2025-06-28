package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "members")
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    private String gender;

    private LocalDate birthDate;

    private String avatarUrl;

    @Nationalized
    @Column(name = "address", length = 1000) // hoặc bỏ length để Hibernate tự xử lý
    private String address;


    private String phoneNumber;
}