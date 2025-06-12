package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;
import org.datcheems.swp_projectnosmoking.entity.BlogCategory;

import java.time.LocalDateTime;

@Data
public class BlogResponse {
    private String title;
    private String content;
    private String coverImage;
    private BlogCategory category;
    private LocalDateTime createdAt;
}
