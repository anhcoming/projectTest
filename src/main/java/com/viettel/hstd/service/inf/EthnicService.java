package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
        import com.viettel.hstd.dto.hstd.EthnicDTO.*;

public interface EthnicService extends CRUDService<EthnicRequest, EthnicResponse, Long> {
        boolean check(String content);
}