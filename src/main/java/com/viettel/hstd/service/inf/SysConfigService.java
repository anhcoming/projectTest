package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.SysConfigDTO.*;

public interface SysConfigService extends
        CRUDService<SysConfigRequest,
                SysConfigResponse, Long> {
    SysConfigResponse findOneByKey(String key);
}
