package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "CoachReview")
public class CoachReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    private Integer rating;

    private String comment;

    private LocalDate createdAt = LocalDate.now();
}