package org.datcheems.swp_projectnosmoking.mapper;

import org.datcheems.swp_projectnosmoking.dto.response.CoachProfileResponse;
import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CoachMapper {
    public CoachProfileResponse mapToResponse(User user, Coach profile) {
        CoachProfileResponse response = new CoachProfileResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setSpecialization(profile.getSpecialization());
        response.setBio(profile.getBio());
        response.setRating(profile.getRating());
        response.setYearsOfExperience(profile.getYearsOfExperience());
        return response;
    }

}
