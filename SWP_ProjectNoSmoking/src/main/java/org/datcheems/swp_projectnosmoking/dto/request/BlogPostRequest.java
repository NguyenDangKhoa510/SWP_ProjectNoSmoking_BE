package org.datcheems.swp_projectnosmoking.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.datcheems.swp_projectnosmoking.entity.BlogCategory;

import java.time.LocalDateTime;

@Data
public class BlogPostRequest {
    private String title;
    private String content;
    private String coverImage;
    private BlogCategory category;
}
