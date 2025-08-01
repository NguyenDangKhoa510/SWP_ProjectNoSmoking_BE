package org.datcheems.swp_projectnosmoking.mapper;

import org.datcheems.swp_projectnosmoking.dto.request.SmokingLogRequest;
import org.datcheems.swp_projectnosmoking.dto.response.SmokingLogResponse;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.SmokingLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SmokingLogMapper {

    SmokingLogMapper INSTANCE = Mappers.getMapper(SmokingLogMapper.class);

    @Mapping(target = "logId", ignore = true)
    @Mapping(target = "member", source = "member")
    @Mapping(target = "smoked", source = "request.smoked")
    @Mapping(target = "cravingLevel", source = "request.cravingLevel")
    @Mapping(target = "healthStatus", source = "request.healthStatus")

    SmokingLog toEntity(SmokingLogRequest request, Member member);

    @Mapping(target = "userId", source = "member.userId")
    @Mapping(target = "memberName", source = "member.user.fullName")
    @Mapping(target = "previousSmokeCount", ignore = true)
    @Mapping(target = "isImprovement", ignore = true)
    @Mapping(target = "smoked", source = "smoked")
    @Mapping(target = "cravingLevel", source = "cravingLevel")
    @Mapping(target = "healthStatus", source = "healthStatus")
    @Mapping(target = "quitPlanStageId", source = "quitPlanStage.id")
    @Mapping(target = "stageNumber", source = "quitPlanStage.stageNumber")
    SmokingLogResponse toResponse(SmokingLog smokingLog);


    void updateEntityFromRequest(SmokingLogRequest request, @MappingTarget SmokingLog smokingLog);
}

