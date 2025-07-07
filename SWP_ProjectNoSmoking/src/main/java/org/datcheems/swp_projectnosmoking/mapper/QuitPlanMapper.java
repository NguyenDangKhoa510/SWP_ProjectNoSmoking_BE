package org.datcheems.swp_projectnosmoking.mapper;

import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanResponse;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanStageResponse;
import org.datcheems.swp_projectnosmoking.entity.QuitPlan;
import org.datcheems.swp_projectnosmoking.entity.QuitPlanStage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QuitPlanMapper {

    QuitPlanMapper INSTANCE = Mappers.getMapper(QuitPlanMapper.class);

    @Mapping(target = "quitPlanId", source = "id")
    @Mapping(target = "memberId", source = "member.userId")
    @Mapping(target = "coachId", source = "coach.userId")
    QuitPlanResponse toResponse(QuitPlan quitPlan);

    @Mapping(target = "stageId", source = "id")
    QuitPlanStageResponse toStageResponse(QuitPlanStage stage);
}