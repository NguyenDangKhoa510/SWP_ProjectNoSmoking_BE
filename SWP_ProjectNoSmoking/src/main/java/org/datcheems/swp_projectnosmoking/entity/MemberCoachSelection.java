package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_coach_selections")
@Data
@NoArgsConstructor
public class MemberCoachSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectionId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    private LocalDateTime selectedAt;
}

