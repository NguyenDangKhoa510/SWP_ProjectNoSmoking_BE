package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.CoachProfileResponse;
import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.MemberCoachSelection;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.mapper.CoachMapper;
import org.datcheems.swp_projectnosmoking.repository.MemberCoachSelectionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCoachSelectionService {

    private final MemberCoachSelectionRepository selectionRepository;
    private final CoachMapper coachMapper;

    public CoachProfileResponse getCoachProfileBySelectionId(Long selectionId) {
        MemberCoachSelection selection = selectionRepository.findById(selectionId)
                .orElseThrow(() -> new RuntimeException("Selection not found with id: " + selectionId));

        Coach coach = selection.getCoach();

        if (coach == null || coach.getUser() == null) {
            throw new RuntimeException("Coach or User not found for selectionId: " + selectionId);
        }

        User user = coach.getUser();

        return coachMapper.mapToResponse(user, coach);
    }
}
