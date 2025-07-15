package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class BadgeRequest {
    String name;
    String description;
    int condition;
    String type;
    String iconUrl;
    int score;
}
