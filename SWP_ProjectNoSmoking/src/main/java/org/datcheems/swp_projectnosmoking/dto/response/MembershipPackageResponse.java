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
    int Id;
    String Name;
    String Description;
    Double Price;
    Date ReleaseDate;
    Date EndDate;
}
