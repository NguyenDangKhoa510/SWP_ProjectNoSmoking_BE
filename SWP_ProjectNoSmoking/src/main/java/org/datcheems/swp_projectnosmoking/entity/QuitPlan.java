package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "QuitPlan")
public class QuitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "selection_id")
    private Integer selectionId;

    @Column(name = "coach_id")
    private Integer coachId;

    @Column(name = "goal_description", columnDefinition = "TEXT")
    private String goalDescription;

    @Enumerated(EnumType.STRING)
    private PlanStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PlanStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}
