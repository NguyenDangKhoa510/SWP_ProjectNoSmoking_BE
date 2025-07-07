package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "QuitPlan")
@NoArgsConstructor
public class QuitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    private LocalDate startDate;

    private String goal;

    @OneToMany(mappedBy = "quitPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuitPlanStage> stages;
}

