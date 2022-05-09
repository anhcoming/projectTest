package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDetailFileDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressDetailFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecruitmentProgressDetailFileConverter {

    @Mapping(target = "detailId", source = "detailId")
    RecruitmentProgressDetailFileEntity requestToEntity(RecruitmentProgressDetailFileDTO.Request request, Long detailId);
}
