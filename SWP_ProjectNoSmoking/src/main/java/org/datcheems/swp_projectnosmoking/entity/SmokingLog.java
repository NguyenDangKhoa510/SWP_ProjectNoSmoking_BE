package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "smoking_logs")
@NoArgsConstructor
public class SmokingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    private Integer smokeCount;


    @Column(name = "smoked")
    private Boolean smoked;

    @Enumerated(EnumType.STRING)
    @Column(name = "craving_level")
    private CravingLevel cravingLevel;


    @Nationalized
    @Column(name = "health_status", length = 255)
    private String healthStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quit_plan_stage_id")
    private QuitPlanStage quitPlanStage;

    public enum CravingLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @PrePersist
    protected void onCreate() {
        if (logDate == null) {
            logDate = LocalDate.now();
        }
    }

    public enum Frequency {
        DAILY,
        WEEKLY
    }
}