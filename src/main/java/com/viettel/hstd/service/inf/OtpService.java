package com.viettel.hstd.service.inf;

import com.viettel.hstd.core.service.CRUDService;
import com.viettel.hstd.dto.hstd.OtpDTO.*;

public interface OtpService {
        OtpCreateResponse create(OtpCreateRequest request);
        OtpSubmitResponse submit(OtpRequest request);
}