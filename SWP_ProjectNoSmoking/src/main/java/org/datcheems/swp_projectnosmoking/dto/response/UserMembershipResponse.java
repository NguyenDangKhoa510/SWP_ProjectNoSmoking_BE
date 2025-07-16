package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMembershipResponse {
    Long membershipId;
    Long userId;
    String userName;
    Long membershipPackageId;
    String membershipPackageName;
    LocalDate startDate;
    LocalDate endDate;
    String status;
}
