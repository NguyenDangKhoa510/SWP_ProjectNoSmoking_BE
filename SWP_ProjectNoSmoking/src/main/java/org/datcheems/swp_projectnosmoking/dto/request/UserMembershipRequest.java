package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMembershipRequest {
    Long userId;
    Long membershipPackageId;
    LocalDate startDate;
    LocalDate endDate;
    String status;
}
