package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.SysLogDTO;
import com.viettel.hstd.entity.hstd.SysLogEntity;

public interface SysLogService extends CRUDService<SysLogDTO.SysLogRequest, SysLogDTO.SysLogResponse, Long> {
    void create(SysLogEntity request);
}
