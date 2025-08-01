package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "QuitPlanStage")
@NoArgsConstructor
public class QuitPlanStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quit_plan_id", nullable = false)
    private QuitPlan quitPlan;

    @Column(name = "stage_number")
    private Integer stageNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "target_cigarette_count")
    private Integer targetCigaretteCount;

    @Column(name = "advice", columnDefinition = "NVARCHAR(255)")
    private String advice;

    @Column(name = "progress_percentage")
    private Double progressPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuitPlanStageStatus status;

//    ALTER TABLE [NoSmoking].[dbo].[quit_plan_stage]
//    DROP COLUMN [milestone_date],
//            [day],
//            [description],
//            [target_date];

}
