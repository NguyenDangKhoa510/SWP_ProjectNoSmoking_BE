package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Data
@Entity
@Table(name = "coach")
@NoArgsConstructor
public class Coach {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(255)")
    private String specialization;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String bio;

    @Column(name = "years_of_experience")
    private int yearsOfExperience;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Column(name = "image_url", columnDefinition = "NVARCHAR(MAX)")
    private String imageUrl;

    private Double rating;
}

