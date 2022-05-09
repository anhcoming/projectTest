package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.RecruitmentProgressDetailDTO;
import com.viettel.hstd.entity.hstd.RecruitmentProgressDetailEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RecruitmentProgressDetailService {
    Page<RecruitmentProgressDetailDTO.Response> searchByProgressId(RecruitmentProgressDetailDTO.SearchCriteria searchCriteria);

    void save(RecruitmentProgressDetailEntity entity);

    void saveBatch(List<RecruitmentProgressDetailDTO.Request> requests);
}
