package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDetailDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecruitmentProgressDetailConverter {
    List<RecruitmentProgressDetailEntity> toEntities(List<RecruitmentProgressDetailDTO.Request> requests);

    List<RecruitmentProgressDetailEntity> dailyUpdateProjectionsToEntity(List<RecruitmentProgressDetailDTO.DailyUpdateProjection> projections);

    RecruitmentProgressDetailDTO.Response responseProjectionToResponse(RecruitmentProgressDetailDTO.ResponseProjection responseProjection);


}
