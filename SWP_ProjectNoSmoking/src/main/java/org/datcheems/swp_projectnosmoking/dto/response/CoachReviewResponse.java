package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CoachReviewResponse {
    private Long reviewId;
    private Integer rating;
    private String comment;
    private LocalDate createdAt;
    private String reviewerName;
    private String reviewerUsername;

}