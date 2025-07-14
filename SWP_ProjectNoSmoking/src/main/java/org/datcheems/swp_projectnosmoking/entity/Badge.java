package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(name = "tblBadge")
@Data

public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Nationalized
    private String name;
    @Nationalized
    private String description;
    @Nationalized
    private String condition_description;

    @Column(name = "icon_url", columnDefinition = "NVARCHAR(MAX)")
    private String iconUrl;
    
    private int score;
}
