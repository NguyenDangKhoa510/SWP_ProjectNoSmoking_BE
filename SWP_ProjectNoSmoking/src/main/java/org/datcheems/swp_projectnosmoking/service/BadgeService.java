package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.BadgeRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BadgeResponse;
import org.datcheems.swp_projectnosmoking.entity.Badge;
import org.datcheems.swp_projectnosmoking.repository.BadgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public BadgeResponse createBadge(BadgeRequest request) {
        Badge badge = new Badge();
        badge.setName(request.getName());
        badge.setDescription(request.getDescription());
        badge.setCondition_description(request.getCondition_description());
        badge.setIconUrl(request.getIconUrl());
        badge.setScore(request.getScore());
        badge = badgeRepository.save(badge);
        return mapToResponse(badge);
    }

    public List<BadgeResponse> getAllBadges() {
        return badgeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BadgeResponse getBadgeById(long id) {
        Badge badge = badgeRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + id));
        return mapToResponse(badge);
    }

    public BadgeResponse updateBadge(long id, BadgeRequest request) {
        Badge badge = badgeRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Badge not found with id: " + id));
        badge.setName(request.getName());
        badge.setDescription(request.getDescription());
        badge.setCondition_description(request.getCondition_description());
        badge.setScore(request.getScore());
        badge.setIconUrl(request.getIconUrl());
        badge = badgeRepository.save(badge);
        return mapToResponse(badge);
    }

    public void deleteBadge(long id) {
        badgeRepository.deleteById((long) id);
    }

    private BadgeResponse mapToResponse(Badge badge) {
        return BadgeResponse.builder()
                .id(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .condition_description(badge.getCondition_description())
                .score(badge.getScore())
                .build();
    }
}
