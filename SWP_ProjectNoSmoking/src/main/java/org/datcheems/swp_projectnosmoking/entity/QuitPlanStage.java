package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "QuitPlanStage")
public class QuitPlanStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "quit_plan_id")
    private Integer quitPlanId;

    @Column(name = "stage_number")
    private Integer stageNumber;

    @Column(name = "milestone_date")
    private LocalDate milestoneDate;

    @Enumerated(EnumType.STRING)
    private StageStatus status;

    @Column(columnDefinition = "TEXT")
    private String advice;

    public enum StageStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}
