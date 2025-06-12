package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "tbl_SmokingStatus")
@NoArgsConstructor
public class SmokingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private int cigarettesPerDay; // Số điếu 1 ngày
    private String frequency; // Tần suất (ví dụ "2 packs/week")
    private double pricePerPack; // Giá 1 gói
    private LocalDate recordDate; // Ngày ghi nhận
}
