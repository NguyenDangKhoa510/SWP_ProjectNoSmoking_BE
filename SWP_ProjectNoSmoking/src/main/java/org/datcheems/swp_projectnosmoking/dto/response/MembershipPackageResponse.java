package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class MembershipPackageResponse {
    long id;
    String name;
    int duration;
    String description;
    Double price;
    Date releaseDate;
    Date endDate;
}
