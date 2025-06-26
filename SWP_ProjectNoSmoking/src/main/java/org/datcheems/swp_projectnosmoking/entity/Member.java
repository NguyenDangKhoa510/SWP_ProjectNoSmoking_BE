package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String gender;

    private LocalDate birthDate;

    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String phoneNumber;
}