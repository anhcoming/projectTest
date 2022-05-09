package com.viettel.hstd.service.mapper;

import com.viettel.hstd.dto.hstd.InsuranceSessionDTO;
import com.viettel.hstd.entity.hstd.InsuranceSessionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface InsuranceMapper extends EntityMapper<InsuranceSessionDTO.InsuranceSessionResponse, InsuranceSessionEntity> {}
