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
    String Name;
    String Description;
    Double Price;
    Date ReleaseDate;
    Date EndDate;
}
