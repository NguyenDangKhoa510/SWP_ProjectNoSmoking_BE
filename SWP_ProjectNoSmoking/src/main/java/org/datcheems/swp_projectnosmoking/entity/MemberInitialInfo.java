package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "member_initial_info")
@Getter
@Setter
@NoArgsConstructor
public class MemberInitialInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    private Integer yearsSmoking;
    private Integer cigarettesPerDay;

    @Nationalized
    @Column(columnDefinition = "TEXT")
    private String reasonToQuit;

    @Nationalized
    @Column(columnDefinition = "TEXT")
    private String healthStatus;
}
