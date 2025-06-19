package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;
import org.datcheems.swp_projectnosmoking.entity.BlogCategory;
import org.datcheems.swp_projectnosmoking.entity.BlogStatus;

import java.time.LocalDateTime;

@Data
public class BlogResponse {
    private String title;
    private String content;
    private String coverImage;
    private BlogStatus status;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
