package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NeedUsernameResponse {
    private String email;
    private String name;
}
