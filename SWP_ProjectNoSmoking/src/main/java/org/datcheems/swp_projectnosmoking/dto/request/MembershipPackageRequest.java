package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipPackageRequest {
    String name;
    int duration;
    String description;
    Double Price;
    Date releaseDate;
    Date endDate;
}
