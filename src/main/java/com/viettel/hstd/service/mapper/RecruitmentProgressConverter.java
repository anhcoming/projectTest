package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.entity.vps.VhrFutureOrganizationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecruitmentProgressConverter {

    RecruitmentProgressEntity requestToEntity(RecruitmentProgressDTO.RecruitmentProgressRequest request);

    List<RecruitmentProgressEntity> requestsToEntities(List<RecruitmentProgressDTO.RecruitmentProgressRequest> requests);

    void copy(@MappingTarget RecruitmentProgressEntity entity, RecruitmentProgressDTO.RecruitmentProgressRequest request);

    RecruitmentProgressDTO.Response projectionToResponse(RecruitmentProgressDTO.Projection projection);

    RecruitmentProgressDTO.SingleResponse entityToSingleResponse(RecruitmentProgressEntity entity);

    RecruitmentProgressDTO.EmailResponse emailProjectionToEmailResponse(RecruitmentProgressDTO.EmailResponseProjection projection);

    List<RecruitmentProgressEntity> dailyUpdateProjectionsToEntities(List<RecruitmentProgressDTO.DailyUpdateProjection> projections);

    void copyPositionToRequest(@MappingTarget RecruitmentProgressDTO.RecruitmentProgressRequest request,PositionEntity positionEntity);

    @Mapping(source = "code", target = "organizationCode")
    @Mapping(source = "name", target = "organizationName")
    void copyOrganizationToRequest(@MappingTarget RecruitmentProgressDTO.RecruitmentProgressRequest request, VhrFutureOrganizationEntity organizationEntity);

}
