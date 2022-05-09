package com.viettel.hstd.dto.hstd;

import com.viettel.hstd.constant.VOfficeSignType;
import com.viettel.voffice.ws_autosign.service.Vof2EntityUser;

import java.util.List;

public class VOfficeSignDTO {
        public List<VOfficeSignDataDTO> signData;
        public List<Vof2EntityUser> userSign;
        public VOfficeSignType type;
        public List<String> attachmentList;
}
