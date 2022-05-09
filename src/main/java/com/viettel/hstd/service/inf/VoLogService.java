package com.viettel.hstd.service.inf;

import com.viettel.hstd.dto.hstd.VOfficeSignDTO;

public interface VoLogService {
    void logVOSent(VOfficeSignDTO request);
    void logVOReceive(String request);
}
